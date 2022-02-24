package info.kgeorgiy.ja.antonov.bank.person;

import info.kgeorgiy.ja.antonov.bank.account.Account;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface Person extends Remote {

    String getFirstName() throws RemoteException;

    String getLastName() throws RemoteException;

    String getPassport() throws RemoteException;

    Map<String, Account> getAccounts() throws RemoteException;

    Account createAccount(String subId) throws RemoteException;

    Account getAccountBySubId(String subId) throws RemoteException;
}
