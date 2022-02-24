package info.kgeorgiy.ja.antonov.bank.person;

import info.kgeorgiy.ja.antonov.bank.account.Account;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractPerson implements Person {

    protected String firstName;
    protected String lastName;
    protected String passport;
    protected Map<String, Account> accounts;

    public AbstractPerson(){}

    public AbstractPerson(String firstName, String lastName, String passport){
        this.firstName = firstName;
        this.lastName = lastName;
        this.passport = passport;

        this.accounts = new ConcurrentHashMap<>();
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getPassport() {
        return passport;
    }

    @Override
    public Map<String, Account> getAccounts() {
        return accounts;
    }

    @Override
    public Account getAccountBySubId(String subId) throws RemoteException {
        return accounts.get(passport + ":" + subId);
    }
}
