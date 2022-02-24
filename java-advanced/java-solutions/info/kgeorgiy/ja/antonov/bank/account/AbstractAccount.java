package info.kgeorgiy.ja.antonov.bank.account;

import info.kgeorgiy.ja.antonov.bank.person.AbstractPerson;

public abstract class AbstractAccount implements Account {
    private final String id;
    private int amount;

    public AbstractAccount(){
        id = null;
    }

    public AbstractAccount(final String id) {
        this.id = id;
        amount = 0;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public synchronized int getAmount() {
        System.out.println("Getting amount of money for account " + id);
        return amount;
    }

    @Override
    public synchronized void setAmount(final int amount) {
        System.out.println("Setting amount of money for account " + id);
        this.amount = amount;
    }
}
