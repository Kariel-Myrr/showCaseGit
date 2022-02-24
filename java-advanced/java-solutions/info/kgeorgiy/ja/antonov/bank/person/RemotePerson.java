package info.kgeorgiy.ja.antonov.bank.person;

import info.kgeorgiy.ja.antonov.bank.account.Account;
import info.kgeorgiy.ja.antonov.bank.account.RemoteAccount;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemotePerson extends AbstractPerson {

    protected int port;

    public RemotePerson(String firstName, String lastName, String passport, int port){
        super(firstName, lastName, passport);

        this.port = port;
    }

    @Override
    public Account createAccount(String subId) throws RemoteException {
        System.out.println("Creating " + passport + " person's account " + subId);
        final Account account = new RemoteAccount(subId);
        if (accounts.putIfAbsent(subId, account) == null) {
            UnicastRemoteObject.exportObject(account, port);
            return account;
        } else {
            return accounts.get(subId);
        }
    }
}
