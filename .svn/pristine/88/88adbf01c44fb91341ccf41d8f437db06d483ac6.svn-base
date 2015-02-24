package client.potlach.com.potlachandroid.singleton;

import client.potlach.com.potlachandroid.model.Chain;
import client.potlach.com.potlachandroid.model.Gift;
import client.potlach.com.potlachandroid.model.User;

/**
 * Created by thiago on 10/20/14.
 */
public class DataHolder {

    private Chain selectedChain;

    private User selectedUser;

    private Gift selectedGift;

    public Chain getSelectedChain() {
        return selectedChain;
    }

    public void setSelectedChain(Chain selectedChain) {
        this.selectedChain = selectedChain;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}

    public Gift getSelectedGift() {
        return selectedGift;
    }

    public void setSelectedGift(Gift selectedGift) {
        this.selectedGift = selectedGift;
    }
}
