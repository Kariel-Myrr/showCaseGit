package info.kgeorgiy.ja.antonov.bank.account;

import java.io.Serializable;

public class LocalAccount extends AbstractAccount implements Serializable {
    public LocalAccount(String id) {
        super(id);
    }
}
