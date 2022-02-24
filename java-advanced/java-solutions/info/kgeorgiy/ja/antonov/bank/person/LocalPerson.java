package info.kgeorgiy.ja.antonov.bank.person;

import info.kgeorgiy.ja.antonov.bank.account.Account;
import info.kgeorgiy.ja.antonov.bank.account.LocalAccount;

import java.io.Serializable;
import java.rmi.RemoteException;

public class LocalPerson extends AbstractPerson implements Serializable {

    @Override
    public Account createAccount(String subId) throws RemoteException {
        Account ne = new LocalAccount(passport + ":" + subId);
        Account a = accounts.putIfAbsent(subId, ne);
        return a == null ? ne : a;
    }
}
