package info.kgeorgiy.ja.antonov.implementor.main;


import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

//java -cp . -p . -m info.kgeorgiy.java.advanced.implementor class info.kgeorgiy.ja.antonov.implementor.main.Implementor

public class Implementor implements JarImpler {

    /*
    TODO
     -how to avoid + concatination
     -file setup
     -documentation
     */

    private static final String USAGE = "Try: [-jar]? [ full class/interface name ]";

    //all modifiers that could be before then we implementing them: methods, constructors
    private static final HashMap<String, Function<Integer, Boolean>> modifiers = new HashMap<>(Map.of(
            "public", Modifier::isPublic,
            "protected", Modifier::isProtected,
            "strictfp", Modifier::isStrict,
            "synchronized", Modifier::isSynchronized
    ));

    static private final String SEP = System.lineSeparator();
    static private final String OFB = "{";
    static private final String CFB = "}";
    static private final String SP = " ";

    private Class<?> aClass;
    private boolean isInterface;
    private HashMap<String, HashMap<List<Class<?>>, Method>> methods;

    private String fileDir;
    private String javaFileName;
    private String classFileName;


    private final Predicate<Method> setFilter = m -> {
        String name = m.getName();
        if (methods.containsKey(name)) {
            HashMap<List<Class<?>>, Method> map = methods.get(name);
            return map.putIfAbsent(List.of(m.getParameterTypes()), m) == null;
        } else {
            HashMap<List<Class<?>>, Method> map = new HashMap<>();
            map.put(List.of(m.getParameterTypes()), m);
            methods.put(name, map);
            return true;
        }
    };

    private void setUpFileNames(Path root) {
        this.fileDir = root + File.separator + aClass.getPackageName().replace(".", File.separator);

        final String mainNamePart = fileDir + File.separator + aClass.getSimpleName() + "Impl";

        this.javaFileName = mainNamePart + ".java";
        this.classFileName = mainNamePart + ".class";
    }

    @Override
    public void implement(Class<?> aClass, Path root) throws ImplerException {
        int mod = aClass.getModifiers();

        //we cannot implement final, private, primitive, anonymous, enum
        assertErr(Modifier.isFinal(mod), "final");
        assertErr(Modifier.isPrivate(mod), "private");
        assertErr(aClass.isPrimitive(), "primitive");
        assertErr(aClass.isAnonymousClass(), "anonymous");
        assertErr(Enum.class.isAssignableFrom(aClass), "enum's");


        this.aClass = aClass;
        this.isInterface = this.aClass.isInterface();
        this.methods = new HashMap<>();
        setUpFileNames(root);

        final String reflection = createReflection();

        if (!Files.exists(Path.of(fileDir))) {
            try {
                Files.createDirectories(Path.of(fileDir));
            } catch (IOException e) {
                ImplerException exc = new ImplerException("Can't create directory for file. Dir path: " + fileDir);
                exc.addSuppressed(e);
                throw exc;
            }
        }

        try (BufferedWriter out = new BufferedWriter(new FileWriter(javaFileName, StandardCharsets.UTF_8))) {

            out.write(reflection);
        } catch (IOException e) {
            ImplerException exc = new ImplerException("Troubles with writing to " + javaFileName);
            exc.addSuppressed(e);
            throw exc;
        }
    }

    @Override
    public void implementJar(Class<?> aClass, Path jarPath) throws ImplerException {
        implement(aClass, Path.of("." + File.separator));
        try {
            compileClassFile();
            try {
                if (!Files.exists(jarPath)) {
                    Files.createFile(jarPath);
                }
                try (JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(jarPath.toFile()));
                     InputStream classInputStream = new FileInputStream(classFileName);) {
                    final String inJarFileName = String.join(
                            File.separator,
                            aClass.getPackageName().replace(".", "/"),
                            aClass.getSimpleName() + "Impl.class");
                    final JarEntry file = new JarEntry(inJarFileName);
                    jarOut.putNextEntry(file);
                    jarOut.write(classInputStream.readAllBytes());
                    jarOut.closeEntry();
                } catch (IOException e) {
                    ImplerException exc = new ImplerException("Problems with jarOutPutStream or classInputStream");
                    exc.addSuppressed(e);
                    throw exc;
                }
            } finally {
                deleteFile(classFileName);
            }

        } catch (IOException e) {
            ImplerException exc = new ImplerException("Can't implement aClass");
            exc.addSuppressed(e);
            throw exc;
        } finally {
            deleteFile(javaFileName);
        }
    }

    private void deleteFile(String javaFileName) {
        try {
            Files.delete(Path.of(javaFileName));
        } catch (IOException e) {
            System.err.println( "Can't clean file: " + javaFileName);
        }
    }

    private void compileClassFile() throws IOException {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        try {
            final String[] args = {
                    javaFileName,
                    "-cp",
                    aClass.getProtectionDomain().getCodeSource().getLocation().toURI().toString()
            };
            final int exitCode = compiler.run(null, null, null, args);
            if (exitCode != 0) {
                throw new IOException("Compilation fail");
            }
        } catch (URISyntaxException e) {
            IOException exc = new IOException("Incorrect URI syntax while getting aClass location");
            exc.addSuppressed(e);
            throw exc;
        }
    }

    public String createReflection() throws ImplerException {

        String packageSection = mSection(mPackage());
        String classSection = mSection(mClass());

        return packageSection + classSection;
    }


    private String mPackage() {
        return String.format("package %s;", aClass.getPackageName());
    }

    private String mClass() throws ImplerException {
        return mHeader()
                + mFigureBrackets(mConstructors(), mMethods());
    }

    //модификаторы class имя
    private String mHeader() {

        return String.format("public class %sImpl %s %s ",
                aClass.getSimpleName(),
                isInterface ? "implements" : "extends",
                aClass.getCanonicalName());

    }

    private String mConstructors() throws ImplerException {

        String constructors = Arrays.stream(aClass.getDeclaredConstructors())
                .filter(c -> isOverridable(c.getModifiers()))
                .map(this::mConstructor)
                .collect(Collectors.joining());

        assertErr(!isInterface && constructors.equals(""), ",no accessible constructors ,"); //TODO

        return constructors;
    }

    private String mConstructor(Constructor<?> constructor) {

        String modifiers = mModifiers(constructor.getModifiers());
        String name = constructor.getDeclaringClass().getSimpleName();
        String parameters = mParameters(constructor.getParameters());
        String exceptions = mThrows(constructor.getExceptionTypes());
        String body = mConstructorBody(constructor);

        return String.format("%s %sImpl (%s) %s %s%s",
                modifiers,
                name,
                parameters,
                exceptions,
                body,
                SEP);
    }

    private String mConstructorBody(Constructor<?> constructor) {
        return mFigureBrackets("super(" + Arrays.stream(constructor.getParameters()).map(Parameter::getName).collect(Collectors.joining(", ")) + ");");
    }

    private String mMethods() {
        return mMethods(aClass, aClass.getMethods()) +
                mMethods(aClass);
    }

    private String mMethods(Class<?> curClass, Method[] methodArr) {
        return Arrays.stream(methodArr)
                .filter(m -> isOverridableForAClass(curClass, m.getModifiers()))
                .filter(setFilter)
                .map(this::mMethod)
                .collect(Collectors.joining());
    }

    private String mMethods(Class<?> curClass) {

        if (curClass == null) {
            return "";
        }

        return mMethods(curClass, curClass.getDeclaredMethods()) +
                mMethods(curClass.getSuperclass());

    }

    private boolean isOverridableForAClass(Class<?> curClass, int mod) {
        if (curClass.equals(aClass)) {
            return isOverridable(mod);
        }
        return !Modifier.isPublic(mod) && (curClass.getPackage().equals(aClass.getPackage()) ? isOverridable(mod) : isOverridableNotSamePackage(mod));
    }

    private boolean isOverridable(int mod) {
        return (!Modifier.isStatic(mod) && !Modifier.isPrivate(mod) && !Modifier.isFinal(mod) && !Modifier.isNative(mod));
    }

    private boolean isOverridableNotSamePackage(int mod) {
        return Modifier.isProtected(mod) && isOverridable(mod);
    }

    //модификаторы возвращаемый_тип имя ( аргументы ) исключения
    private String mMethod(Method method) {

        String modifiers = mModifiers(method.getModifiers());
        String returnType = method.getReturnType().getCanonicalName();
        String name = method.getName();
        String parameters = mParameters(method.getParameters());
        String exceptions = mThrows(method.getExceptionTypes());
        String body = mMethodBody(method);

        return String.format("%s %s %s (%s) %s %s%s",
                modifiers,
                returnType,
                name,
                parameters,
                exceptions,
                body,
                SEP);
    }

    private String mThrows(Class<?>[] exceptions) {
        if (exceptions.length == 0) {
            return "";
        }

        return " throws " +
                Arrays.stream(exceptions).map(Class::getCanonicalName).collect(Collectors.joining(", "));
    }

    private String mMethodBody(Method method) {
        String returnStatement = "";

        Class<?> returnType = method.getReturnType();
        if (!returnType.equals(void.class)) {
            if (returnType.isPrimitive()) {
                returnStatement = "return " + PrimitiveDefaults.getDefaultValue(returnType) + ";";
            } else {
                returnStatement = "return null;";
            }
        }

        return mFigureBrackets(returnStatement);

    }

    private String mParameters(Parameter[] parameters) {
        return Arrays.stream(parameters)
                .map(p -> p.getType().getCanonicalName() + SP +
                        p.getName())
                .collect(Collectors.joining(", "));
    }

    private String mModifiers(int modifiers) {

        return Implementor.modifiers.entrySet()
                .stream().filter(e -> e.getValue().apply(modifiers))
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(SP));

    }

    private void assertErr(boolean flag, String adj) throws ImplerException {
        if (flag) {
            throw new ImplerException("Cannot implement/extend " + adj + " interface/class.");
        }
    }

    private String mSection(String in) {
        return SEP + in + SEP;
    }

    private String mFigureBrackets(String... in) {
        return OFB + Arrays.stream(in).map(this::mSection).collect(Collectors.joining()) + CFB;
    }

    public static void main(String[] args) {
        if (args.length > 2) {
            System.err.println("Incorrect amount of arguments.");
            System.err.println(USAGE);
            return;
        }

        String className;
        boolean isJar;

        if (args.length == 2 && args[0].equals("-jar")) {
            isJar = true;
            className = args[1];
        } else if (args.length == 1) {
            className = args[0];
            isJar = false;
        } else {
            System.err.println("Incorrect format.");
            System.err.println(USAGE);
            return;
        }

        Class<?> aClass;


        try {
            aClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found");
            System.err.println(e.getMessage());
            return;
        }

        Implementor implementor = new Implementor();

        try {
            if (isJar) {
                implementor.implementJar(aClass, Path.of(aClass.getPackageName().replace(".", "/") + ".jar"));
            } else {
                implementor.implement(aClass, Path.of("./"));
            }
        } catch (ImplerException e) {
            System.err.println("Implementation went wrong");
            System.err.println(e.getMessage());
        }
    }
}
