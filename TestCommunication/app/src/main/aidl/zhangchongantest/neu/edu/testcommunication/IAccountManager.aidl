// IAccountManager.aidl
package zhangchongantest.neu.edu.testcommunication;

// Declare any non-default types here with import statements
import zhangchongantest.neu.edu.testcommunication.Account;
interface IAccountManager {
    List<Account> getAccounts();
    void addAccount(in Account account);
}
