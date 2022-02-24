package info.kgeorgiy.ja.antonov.bank.bank;

import info.kgeorgiy.ja.antonov.bank.account.Account;
import info.kgeorgiy.ja.antonov.bank.account.RemoteAccount;
import info.kgeorgiy.ja.antonov.bank.person.Person;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RemoteBank implements Bank {
    private final int port;
    private final ConcurrentMap<String, Account> accounts = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Person> persons = new ConcurrentHashMap<>();

    public RemoteBank(final int port) {
        this.port = port;
    }

    @Override
    public Account createAccount(final String id) throws RemoteException {
        System.out.println("Creating account " + id);
        final Account account = new RemoteAccount(id);
        if (accounts.putIfAbsent(id, account) == null) {
            UnicastRemoteObject.exportObject(account, port);
            return account;
        } else {
            return getAccount(id);
        }
    }

    @Override
    public Account getAccount(final String id) {
        System.out.println("Retrieving account " + id);
        return accounts.get(id);
    }

    @Override
    public Person getPersonByPassport(String passport, Boolean isLocal) throws RemoteException {
        return null;
    }

    @Override
    public Person createPerson(String firstName, String lastName, String passport) throws RemoteException {
        return null;
    }
}
