package info.kgeorgiy.ja.antonov.student;

import info.kgeorgiy.java.advanced.student.*;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StudentDB implements GroupQuery {

    private final Comparator<Student> orderByName = Comparator.comparing(Student::getLastName)
            .thenComparing(Student::getFirstName)
            .reversed()
            .thenComparing(Student::getId);

    private <T, R> Predicate<T> filedEquals(Function<T, R> getter, R ind) {
        return (T t) -> getter.apply(t).equals(ind);
    }

    private <T> List<T> getField(List<Student> list, Function<Student, T> fieldGetter) {
        return list
                .stream()
                .map(fieldGetter)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getFirstNames(List<Student> list) {
        return getField(list, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(List<Student> list) {
        return getField(list, Student::getLastName);
    }

    @Override
    public List<GroupName> getGroups(List<Student> list) {
        return getField(list, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(List<Student> list) {
        return getField(list, s -> s.getFirstName() + " " + s.getLastName());
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> list) {
        return list.stream()
                .map(Student::getFirstName)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String getMaxStudentFirstName(List<Student> list) {
        return list.stream().max(Comparator.comparing(Student::getId)).map(Student::getFirstName).orElse("");
    }

    private List<Student> sortStudentByComp(Collection<Student> collection, Comparator<Student> comp) {
        return collection.stream()
                .sorted(comp)
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> collection) {
        return sortStudentByComp(collection, Comparator.comparing(Student::getId));
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> collection) {
        return sortStudentByComp(collection, orderByName);
    }


    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> collection, GroupName groupName) {
        return collection.stream()
                .filter(filedEquals(Student::getGroup, groupName))
                .collect(Collectors.toMap(Student::getLastName, Student::getFirstName,
                        BinaryOperator.maxBy(Comparator.comparing(String::toString).reversed())));

    }

    private Stream<Student> streamFindStudentsByFilterInCompOrder(Collection<Student> collection, Predicate<Student> filter, Comparator<Student> comp) {
        return collection.stream()
                .filter(filter)
                .sorted(comp);
    }

    private <T> List<Student> findStudentsByFieldAndIndicatorInCompOrder(Collection<Student> collection, Function<Student, T> field, T ind, Comparator<Student> comp) {
        return streamFindStudentsByFilterInCompOrder(collection, filedEquals(field, ind), comp).collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> collection, String s) {
        return findStudentsByFieldAndIndicatorInCompOrder(collection, Student::getFirstName, s, orderByName);
    }


    @Override
    public List<Student> findStudentsByGroup(Collection<Student> collection, GroupName groupName) {
        return findStudentsByFieldAndIndicatorInCompOrder(collection, Student::getGroup, groupName, orderByName);
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> collection, String s) {
        return findStudentsByFieldAndIndicatorInCompOrder(collection, Student::getLastName, s, orderByName);
    }

    /*==========GroupQuery methods=========*/

    private Stream<Map.Entry<GroupName, LinkedList<Student>>> groupGroupsByFieldToStream(Collection<Student> collection, Comparator<Student> comp) {
        return collection.stream()
                .sorted(comp)
                .collect(Collectors
                        .groupingBy(Student::getGroup, Collectors.toCollection(LinkedList::new))
                )
                .entrySet()
                .stream();
    }


    private Stream<Group> getStreamGroupsByField(Collection<Student> collection, Comparator<Student> comp) {
        return groupGroupsByFieldToStream(collection, comp)
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new Group(e.getKey(), e.getValue()));
    }

    private List<Group> getGroupsByField(Collection<Student> collection, Comparator<Student> comp) {
        return getStreamGroupsByField(collection, comp).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<Group> getGroupsByName(Collection<Student> collection) {
        return getGroupsByField(collection, orderByName);
    }

    @Override
    public List<Group> getGroupsById(Collection<Student> collection) {
        return getGroupsByField(collection, Comparator.comparingInt(Student::getId));
    }

    @Override
    public GroupName getLargestGroup(Collection<Student> collection) {
        return getStreamGroupsByField(collection, orderByName)
                .max(Comparator.comparing(Group::getStudents,
                        Comparator.comparingInt(List::size))
                        .thenComparing(Group::getName))
                .stream()
                .map(Group::getName)
                .findFirst()
                .orElse(null);
    }

    @Override
    public GroupName getLargestGroupFirstName(Collection<Student> collection) {
        return groupGroupsByFieldToStream(collection, orderByName)
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue()
                        .stream()
                        .map(Student::getFirstName)
                        .collect(Collectors.toSet())
                ))
                .max(entryValueSetComparator)
                .stream()
                .map(AbstractMap.SimpleEntry::getKey)
                .findFirst()
                .orElse(null);
    }

    private final Comparator<Set<String>> setSizeComparator = Comparator.comparing(Set::size);
    private final Comparator<Map.Entry<GroupName, Set<String>>> entryValueSetSizeComparator = Map.Entry.comparingByValue(setSizeComparator);
    private final Comparator<Map.Entry<GroupName, Set<String>>> entryValueSetComparator = entryValueSetSizeComparator.reversed().thenComparing(Map.Entry::getKey).reversed();

}
