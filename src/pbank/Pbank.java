package pbank;

import global.cloudcoin.ccbank.Authenticator.Authenticator;
import global.cloudcoin.ccbank.Authenticator.AuthenticatorResult;
import global.cloudcoin.ccbank.Backupper.BackupperResult;
import global.cloudcoin.ccbank.Echoer.Echoer;
import global.cloudcoin.ccbank.Eraser.EraserResult;
import global.cloudcoin.ccbank.Exporter.Exporter;
import global.cloudcoin.ccbank.Exporter.ExporterResult;
import global.cloudcoin.ccbank.FrackFixer.FrackFixer;
import global.cloudcoin.ccbank.FrackFixer.FrackFixerResult;
import global.cloudcoin.ccbank.Grader.Grader;
import global.cloudcoin.ccbank.Grader.GraderResult;
import global.cloudcoin.ccbank.LossFixer.LossFixerResult;
import global.cloudcoin.ccbank.Receiver.ReceiverResult;
import global.cloudcoin.ccbank.Sender.SenderResult;
import global.cloudcoin.ccbank.ServantManager.ServantManager;
import global.cloudcoin.ccbank.ShowCoins.ShowCoins;
import global.cloudcoin.ccbank.ShowCoins.ShowCoinsResult;
import global.cloudcoin.ccbank.Unpacker.Unpacker;
import global.cloudcoin.ccbank.Vaulter.VaulterResult;
import global.cloudcoin.ccbank.core.AppCore;
import global.cloudcoin.ccbank.core.CallbackInterface;
import global.cloudcoin.ccbank.core.Config;
import global.cloudcoin.ccbank.core.DNSSn;
import global.cloudcoin.ccbank.core.GLogger;
import global.cloudcoin.ccbank.core.ServantRegistry;
import global.cloudcoin.ccbank.core.Wallet;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLEncoder;
import java.util.*;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.imageio.ImageIO;
import javax.imageio.spi.ServiceRegistry;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



/**
 * 
 */
public class Pbank {

    String ltag = "Pbank";
    
    final static int SCREEN_EXIT = 0;
    final static int SCREEN_MAIN = 1;
    final static int SCREEN_SELECT_WALLET = 2;
    final static int SCREEN_CREATE_WALLET = 3;
    final static int SCREEN_MAIN_WALLET = 4;
    final static int SCREEN_DEPOSIT = 5;
    final static int SCREEN_WITHDRAW = 6;
    final static int SCREEN_SHOW_COINS = 7;
    final static int SCREEN_SHOW_TRANSACTIONS = 8;
    final static int SCREEN_TRANSFER = 9;
    final static int SCREEN_DEPOSITING = 10;
    final static int SCREEN_SHOW_COINS_RESULT = 11;
    final static int SCREEN_WITHDRAW_RESULT = 12;
    final static int SCREEN_WITHDRAW_RESULT1 = 13;
    final static int SCREEN_CREATE_SKYWALLET = 14;
    final static int SCREEN_BACKUP_WALLET = 15;
    final static int SCREEN_TRANSFER_RESULT = 16;

    
    int currentScreen;
    String currentWallet;
    String currentMessage;
    String currentError;
    String currentImportStr;
    
    
    String currentDstWallet;
    
    
    int[][] counters;
    
    int cbState;
    
    final static int CB_STATE_INIT = 1;
    final static int CB_STATE_RUNNING = 2;
    final static int CB_STATE_DONE = 3;
    
    
    int tw = 450;
    int th = 800;
    
    JDialog xframeBank, xframeImport, xframeExport;
    JButton exJpg, exStack;
    Component currentImportComponent;
    Component savedCurrentImport;
    
    int exportType;
    
 
    JFrame mainFrame;
    
    ServantRegistry sr;
    ServantManager sm;
    
    final static int ECHO_RESULT_DOING = 1;
    final static int ECHO_RESULT_DONE = 2;
    
    int echoResult;
    
    final static int DIALOG_NONE = 0;
    final static int DIALOG_IMPORT = 1;
    final static int DIALOG_BANK = 2;
    final static int DIALOG_EXPORT = 3;
     
    final static int IMPORT_STATE_INIT = 1;
    final static int IMPORT_STATE_UNPACKING = 2;
    final static int IMPORT_STATE_IMPORT = 3;
    final static int IMPORT_STATE_DONE = 4;
    
    int importState;
    int requestedDialog;
    
    Authenticator at;
    
    int idx, idy;
    
    JProgressBar pbar;
    JLabel jraida;
    
    JTextField exportTextField;
    
    private int statToBankValue, statToBank, statFailed;
    
    ArrayList<String> exportedFilenames;
    
    WLogger wl;
    
    
 
    public void error(String msg) {
        System.out.println("Error: " + msg);
        System.exit(1);
    }
    
    public void unsetError() {
        setError(null);
    }
    
    public void setError(String error) {
        currentError = error;
    }
    
    public void setScreen(int screen) {
        currentScreen = screen;
    }
    
    public void setWallet(String wallet) {
        currentWallet = wallet;
        sm.setActiveWallet(wallet);
    }
    
    public void initSystem() {
        wl = new WLogger();
        
        
        String home = System.getProperty("user.home");
        
        sm = new ServantManager(wl, home);
        if (!sm.init()) 
            error("Failed to init ServantManager");
        
        setScreen(SCREEN_MAIN);
        
        cbState = CB_STATE_INIT;
        
        requestedDialog = DIALOG_NONE;
        importState = IMPORT_STATE_INIT;
        exportType = Config.TYPE_STACK;
    }   
     
    public void initSystemUser(String user, String email, String password) {
        sm.initUser(user, email, password);
            
        AppCore.copyTemplatesFromJar(user);
    }
    
    
    
    
    
   

    

  
    
    
    
    
    private void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                //String[] cls = new String[] {"cmd.exe", "/c", "cls"};
                //Runtime.getRuntime().exec(cls); 
                //for (int i = 0; i < 250; ++i) System.out.println();
                new ProcessBuilder("cmd.exe", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");  
                System.out.flush();  
                Runtime.getRuntime().exec("clear");
            }
        } catch (final Exception e) {
          
        }
    }
    
    private void showTitle(String title) {
        clearConsole();
        System.out.println();
        System.out.println("*** CloudCoin Console Client ***");
        System.out.println();
        System.out.println(title);
        System.out.println();   
        
        if (currentError != null)
            showError(currentError);
        
    }
    
    private void showError(String error) {
        System.out.println();
        System.out.println("ERROR: " + error);
        System.out.println();
        
        unsetError();
    }
    
    private void waitForKey() {
        System.out.println();
        System.out.println("Press Enter to continue...");
        readItem();
    }
    
    private void showMessage(String message) {
        clearConsole();
        System.out.println();
        System.out.println(message);
        System.out.println();
        System.out.println("Press Enter to continue...");
        readItem();
    }
    
    
    private void showCursor() {
        System.out.println();
        System.out.print(">>>");
    }
    
    private void showBasicScreen(String[] items, int[] screens) {
        int i;
        
        if (items.length != screens.length)
            return;
        
        for (i = 0; i < items.length; i++) {
            System.out.println((i + 1) + ". " + items[i]);
        }
        
        showCursor();
        String val = readItem();
        
        int ival;
        try {
            ival = Integer.parseInt(val);
        } catch (NumberFormatException e) {
            setError("Invalid input");
            return;
        }
        
        if (ival == 0 || ival > items.length) {
            setError("Invalid input");
            return;
        }
        
        setScreen(screens[ival - 1]);
        
    }
    
    private void showMainScreen() {
        showTitle("Select action");
        
        showBasicScreen(new String[] {
            "Select Wallet", "Create Wallet", "Exit"
        }, new int[] { SCREEN_SELECT_WALLET, SCREEN_CREATE_WALLET, SCREEN_EXIT });
    }
    
    private void showCreateSkyWalletScreen() {
        String path;
        
        showTitle("Create Sky Wallet");
        
        System.out.println("Enter the name of the wallet");
        showCursor();
        String name = readItem();
        
        DNSSn d = new DNSSn(name, wl);
      
      /*  if (d.recordExists()) {
            setError("DNS record already exists");
            setScreen(SCREEN_MAIN_WALLET);
            return;
        }
        
        */
                
        System.out.println("Enter the path of the ID Coin: ");
        showCursor();
        
        path = readItem();
        /*
        if (!d.setRecord(path, sm.getSR())) {
            setError("Failed to set record. Check if the coin is valid");
            setScreen(SCREEN_MAIN_WALLET);
            return;
        }
        */
        String newFileName = name + ".stack";
        
        
        if (!AppCore.moveToFolderNewName(path, Config.DIR_ID, currentWallet, newFileName)) {
            setError("Failed to move ID coin");
            setScreen(SCREEN_MAIN_WALLET);
            return;
        }
        
        
        
        showMessage("The SkyWallet has been created");
        setScreen(SCREEN_MAIN_WALLET);
    }
    
    public void showWalletBackupScreen() {
        String path;
        
        if (sm.getActiveWallet().isSkyWallet()) {
            setError("You cant backup SkyWallet");
            setScreen(SCREEN_MAIN_WALLET);
            return;
        }
        
        showTitle("Backup Wallet. Please type the destination folder");
        showCursor();
        
        path = readItem();
        File f = new File(path);
        if (!f.exists()) {
            setError("Folder does not exist");
            setScreen(SCREEN_MAIN_WALLET);
            return;
        }
        
        sm.startBackupperService(path, new BackupperCb());
        
        showMessage("The backup has been scheduled");
        setScreen(SCREEN_MAIN_WALLET);
    }
    
    private void showCreateWalletScreen() {
        String val, email, wallet, password1, password2;
        showTitle("Create wallet. Enter the name of the wallet: ");
        showCursor();
        
        wallet = readItem();
        if (wallet.length() > Config.MAX_WALLET_LENGTH) {
            setError("The length is too big");
            return;
        }
        
        if (wallet.contains("\\") || wallet.contains("/")) {
            setError("Invalid chacaters");
            return;
        }

        System.out.print("Enter the recovery email. Leave blank if not needed");
        showCursor();
       
        email = readItem();
            
        System.out.print("Enter the encryption password. Leave blank if not needed");
        showCursor();
        
        password1 = readItem();
        if (!password1.equals("")) {
            System.out.println("Repeat password");
            showCursor();
            password2 = readItem();
            if (!password1.equals(password2)) {
                setError("Password mismatch");
                return;
            }
        }
        
        initSystemUser(wallet, email, password1);
        
        showMessage("The wallet has been created");
        
        setScreen(SCREEN_MAIN);
    }
    
    
    
    private void showWalletsScreen() {
        showTitle("Wallets");
        
        System.out.println("R - recovery enabled, E - encrypted, S - Sky Wallet");
        System.out.println();
        
        Wallet[] wallets = sm.getWallets();
        for (int i = 0; i < wallets.length; i++) {
            Wallet w = wallets[i];
                 
            String prefix = "";
            if (!w.getEmail().equals("")) 
                prefix += "R";
            
            if (w.isEncrypted())
                prefix += "E";
            
            if (w.isSkyWallet())
                prefix += "S";
            
            System.out.print((i + 1)+ ". " + wallets[i].getName());
            if (!prefix.equals(""))
                System.out.print(" [ " + prefix + " ]");
            
            System.out.println();
        }
        
        System.out.println((wallets.length + 1) + ". Back");
        showCursor();
        
        int idx;
        String val = readItem();
        
        try {
            idx = Integer.parseInt(val);
        } catch (NumberFormatException e) {
            setError("Invalid Input");
            return;
        }
        
        if (idx == wallets.length + 1) {
            setScreen(SCREEN_MAIN);
            return;
        }
        
        if (idx <= 0 || idx > wallets.length) {
            setError("Invalid Wallet");
            return;
        }
              
        if (currentWallet == null || !wallets[idx - 1].getName().equals(currentWallet)) {
            System.out.println("w="+wallets[idx-1].getName());
            setWallet(wallets[idx - 1].getName());     
            if (sm.getActiveWallet().isEncrypted()) {
                System.out.println("Unlock the wallet. Type password:");
                showCursor();
                String password = readItem();
                
                sm.getActiveWallet().setPassword(password);
            }
        }
        
        setScreen(SCREEN_MAIN_WALLET);
    }
    
    private void showWalletMainScreen() {    
        sm.startEchoService(new EchoCb());
        
        showTitle("Select action for wallet " + currentWallet);
        
        String[] ws;
        int[] rv;
        if (sm.getActiveWallet().isSkyWallet()) {
            ws = new String[] {"Deposit Coins", "Show Coins", 
            "Withdraw Coins", "Transfer Coins", "Show Transactions", 
            "Backup", "Back" };
        
            rv = new int[] {  SCREEN_DEPOSIT, SCREEN_SHOW_COINS, SCREEN_WITHDRAW,
            SCREEN_TRANSFER, SCREEN_SHOW_TRANSACTIONS,  
            SCREEN_BACKUP_WALLET, SCREEN_SELECT_WALLET 
            };
        } else {
            ws = new String[] {"Deposit Coins", "Show Coins", 
            "Withdraw Coins", "Transfer Coins", "Show Transactions", "Create Sky Wallet", 
            "Backup", "Back"
            };
            rv = new int[] {  SCREEN_DEPOSIT, SCREEN_SHOW_COINS, SCREEN_WITHDRAW,
            SCREEN_TRANSFER, SCREEN_SHOW_TRANSACTIONS, SCREEN_CREATE_SKYWALLET, 
            SCREEN_BACKUP_WALLET, SCREEN_SELECT_WALLET 
            };
        }
        
        showBasicScreen(ws, rv);      
    }
    
    private void showWalletTransactionsScreen() {
        showTitle("Transactions");
        
        Wallet w = sm.getActiveWallet();
        String[][] ts = w.getTransactions();
        if (ts == null) {
            setError("Failed to get transactions");
            setScreen(SCREEN_MAIN_WALLET);
            return;
        }
        
        System.out.format("%32s    %10s   %10s   %10s   %10s", "Memo", "Date", "Deposit", "Withdraw", "Total");
        System.out.println();
        for (int i = 0; i < ts.length; i++) {
            System.out.format("%32s  %12s   %10s   %10s   %10s", ts[i][0], ts[i][1], ts[i][2], ts[i][3], ts[i][4]);
            System.out.println();
        }
        
        setScreen(SCREEN_MAIN_WALLET);
        waitForKey();
    }
    
    public void showWalletDepositScreen() {
        if (!sm.isEchoerFinished()) {
            showMessage("Echoer is not finished. Please wait");
            return;
        }
        
        showMessage("Put your coins to the " + AppCore.getUserDir(Config.DIR_IMPORT, currentWallet) + ""
                + "\nDo not interrupt the process");
        
        importState = IMPORT_STATE_UNPACKING;
        
        sm.startUnpackerService(new UnpackerCb());
        
        setScreen(SCREEN_DEPOSITING);
        
        
    }
    
    public void doSleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            
        }
    }
    
    public void showWalletDepositingScreen() {
        showTitle("Depositing Coins");
        
        //while (true) {
        if (importState == IMPORT_STATE_UNPACKING) {
            System.out.println("Unpacking...");
            doSleep(200);
            return;                
        }
            
        if (importState == IMPORT_STATE_IMPORT) {
            System.out.println("Importing...");
            System.out.println("stat");
            System.out.println(currentImportStr);
            doSleep(200);
            return;
        }
           
        if (importState == IMPORT_STATE_DONE) {
            System.out.println("Total coins moved to Bank: " + statToBankValue);
            System.out.println("Total authentic coins: " + statToBank);
            System.out.println("Total counterfeit: " + statFailed);
            
            waitForKey();
            
            setScreen(SCREEN_MAIN_WALLET);
            return;
        }
    }
    
    public void showWalletCoinsScreen() {
        cbState = CB_STATE_INIT;     
        requestedDialog = DIALOG_BANK;
        
        if (!sm.getActiveWallet().isSkyWallet())
            sm.startShowCoinsService(new ShowCoinsCb());
        
        setScreen(SCREEN_SHOW_COINS_RESULT);
    }
    
    public void showWalletCoinsResultScreen() {
        if (cbState != CB_STATE_DONE) {
            doSleep(200);
            return;
        }
        
        int totalCnt = AppCore.getTotal(counters[Config.IDX_FOLDER_BANK]) +
			AppCore.getTotal(counters[Config.IDX_FOLDER_FRACKED]) +
                        AppCore.getTotal(counters[Config.IDX_FOLDER_VAULT]);
        
        for (int i = 0; i < AppCore.getDenominations().length; i++) {
            int authCount = counters[Config.IDX_FOLDER_BANK][i] +
		counters[Config.IDX_FOLDER_FRACKED][i] +
                    counters[Config.IDX_FOLDER_VAULT][i];
            
            System.out.println(AppCore.getDenominations()[i] + "s: " + authCount);
        }
        
        System.out.println("Total: " + totalCnt);
                        
        cbState = CB_STATE_INIT;
        
        waitForKey();
        setScreen(SCREEN_MAIN_WALLET);
    }
    
    public void showWalletWithdraw() {
        cbState = CB_STATE_INIT;
        requestedDialog = DIALOG_EXPORT;
        
        sm.startShowCoinsService(new ShowCoinsCb());
        
        setScreen(SCREEN_WITHDRAW_RESULT);
    }
      
    
    public void showWalletWithdrawResult() {
        
        showTitle("Exporting coins");
        
        if (cbState != CB_STATE_DONE) {
            doSleep(200);
            return;
        }
        
        String val;
        int ival;
        int amount;

        
        int type;
        
         
        try {
            System.out.println("Amount to export: ");
            showCursor();
            val = readItem();
            amount = Integer.parseInt(val);
            if (amount < 0) {       
                setError("Invalid input");
                return;
            }
     
            System.out.println("Type of export: " + Config.TYPE_STACK + " - stack; " + Config.TYPE_JPEG + " - jpeg;");
            showCursor();
            val = readItem();
            type = Integer.parseInt(val);
            if (type != Config.TYPE_STACK && type != Config.TYPE_JPEG) {
                setError("Invalid input");
                return;
            }
            
        } catch (NumberFormatException e) {
            setError("Invalid input");
            return;
        }
        
        System.out.println("Enter tag. Leave empty if not needed");
        showCursor();
        String tag = readItem();
        
        if (sm.getActiveWallet().isEncrypted()) {
            sm.startSecureExporterService(type, amount, tag, new ExporterCb());
        } else {
            sm.startExporterService(type, amount, tag, new ExporterCb());
        }
        
        
        cbState = CB_STATE_INIT;
        setScreen(SCREEN_WITHDRAW_RESULT1);
    }
    
    public void showWalletWithdrawResult1() {
        boolean isError = currentError != null;
        
        showTitle("Result of the Export");
        
        if (cbState != CB_STATE_DONE) {
            doSleep(200);
            return;
        }
        
        if (!isError)
            showMessage("Exported successfully");
        
        cbState = CB_STATE_INIT;
        setScreen(SCREEN_MAIN_WALLET);
        
    }
    
    public void showWalletTransferScreen() {
        showTitle("Transfer coins. Please type the destination wallet name");
        
        showCursor();
        currentDstWallet = readItem();
        
        System.out.println("Please type the amount of coins:");
        showCursor();
        String amount = readItem();
        
        int total;
        try {
            total = Integer.parseInt(amount);
        } catch (NumberFormatException e) {
            setError("Invalid amount");
            setScreen(SCREEN_MAIN_WALLET);
            return;
        }
        
        if (total <= 0) {
            setError("Invalid amount");
            setScreen(SCREEN_MAIN_WALLET);
            return;
        }
        
        System.out.println("Memo:");
        showCursor();
        String memo = readItem();
        
        cbState = CB_STATE_INIT;
        
        Wallet dstWallet = sm.getWallet(currentDstWallet);
        if (dstWallet != null && dstWallet.isEncrypted()) {
                System.out.println("Unlock the dst wallet. Type password:");
                showCursor();
                String password = readItem();
                
                dstWallet.setPassword(password);
        }
            
        sm.transferCoins(currentWallet, currentDstWallet, sm.getActiveWallet().getIDCoin(),
                total, memo, new SenderCb(), new ReceiverCb());
        
        
        setScreen(SCREEN_TRANSFER_RESULT);
        
    }
    
    public void showWalletTransferResultScreen() {
        showTitle("Transfer result");
        
        if (cbState != CB_STATE_DONE) {
            doSleep(200);
            return;
        }
                
        cbState = CB_STATE_INIT;
        waitForKey();
        setScreen(SCREEN_MAIN);
        
    }
    
    private String readItem() {
        String val = null;
        try {
            BufferedReader reader =
                   new BufferedReader(new InputStreamReader(System.in));
            val = reader.readLine();
        } catch (IOException e) {
            System.exit(0);
        }
        
        if (val == null) {
            System.exit(0);
        }
        
        return val;
    }
    
    public Pbank() {
      
    
        
        initSystem();
        
        while (true) {
 
            switch (currentScreen) {
                case SCREEN_MAIN:
                    showMainScreen();
                    break;
                case SCREEN_EXIT:
                    System.exit(0);
                    break;
                case SCREEN_SELECT_WALLET:
                    showWalletsScreen();
                    break;
                case SCREEN_MAIN_WALLET:
                    showWalletMainScreen();
                    break;
                case SCREEN_CREATE_WALLET:
                    showCreateWalletScreen();
                    break;
                case SCREEN_SHOW_TRANSACTIONS:
                    showWalletTransactionsScreen();
                    break;
                case SCREEN_DEPOSIT:
                    showWalletDepositScreen();
                    break;
                case SCREEN_DEPOSITING:
                    showWalletDepositingScreen();
                    break;
                case SCREEN_SHOW_COINS:
                    showWalletCoinsScreen();
                    break;
                case SCREEN_SHOW_COINS_RESULT:
                    showWalletCoinsResultScreen();
                    break;
                case SCREEN_WITHDRAW:
                    showWalletWithdraw();
                    break;
                case SCREEN_WITHDRAW_RESULT:
                    showWalletWithdrawResult();
                    break;
                case SCREEN_WITHDRAW_RESULT1:
                    showWalletWithdrawResult1();
                    break;
                case SCREEN_CREATE_SKYWALLET:
                    showCreateSkyWalletScreen();
                    break;
                case SCREEN_BACKUP_WALLET:
                    showWalletBackupScreen();
                    break;
                case SCREEN_TRANSFER:
                    showWalletTransferScreen();
                    break;
                case SCREEN_TRANSFER_RESULT:
                    showWalletTransferResultScreen();
                    break;
                    
                    
            }
            /*
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                
            }*/
            
            if (1 == 0)
                break;
            
        }
  
    }
    
   
    
    public void doEmailReceipt() {
	StringBuilder sb = new StringBuilder();

	Date date = new Date();
	Calendar cal = Calendar.getInstance();
	cal.setTime(date);

	int day = cal.get(Calendar.DAY_OF_MONTH);
	int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

	String dayStr = Integer.toString(day);
	String monthStr = Integer.toString(month);

	if (day < 10)
		dayStr = "0" + dayStr;
	if (day < 10)
		monthStr = "0" + monthStr;

	String dateStr = monthStr + "/" + dayStr + "/" + year;

	sb.append("CloudCoins received ");
	sb.append(" " + dateStr + "\n");
	sb.append("Total authenticated coins (value): ");
	sb.append(statToBankValue);
	sb.append(" of ");
	sb.append(statToBank);
	sb.append(" coins. Failed coins: ");
	sb.append(statFailed);
	sb.append("\n");

	openEmail(sb.toString());
    }
    
    public void openEmail(String body) {
        Desktop desktop;
        if (Desktop.isDesktopSupported()) {
              desktop = Desktop.getDesktop();
              if (desktop.isSupported(Desktop.Action.MAIL)) {
                  try {
                    body = encodeURIComponent(body);
                    String mailURIStr = String.format("mailto:%s?subject=%s&body=%s",
                        "", encodeURIComponent("Import Report"), body);
                    URI mailto = new URI(mailURIStr);
                    desktop.mail(mailto);
                  } catch (URISyntaxException e) {
                      System.out.println("Failed to mail: " + e.getMessage());
                  } catch (IOException e) {
                      System.out.println("Failed to mail: " + e.getMessage());
                  }
              }
        } 
    }
    
    public String encodeURIComponent(String s) {
        String result;

        try {
            result = URLEncoder.encode(s, "UTF-8")
                .replaceAll("\\+", "%20")
                .replaceAll("\\%21", "!")
                .replaceAll("\\%27", "'")
                .replaceAll("\\%28", "(")
                .replaceAll("\\%29", ")")
                .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            result = s;
        }
        
        return result;
    }
    

    

    
    public void doEmailResult() {
        EmailUI e = new EmailUI();
        
        if (exportedFilenames.size() == 0) {
            showError("Internal error");
            return;
        }
        
        if (!Desktop.isDesktopSupported()) {
            showError("Not supported");
            return;
        }
        
        Desktop desktop = Desktop.getDesktop();
        if (!desktop.isSupported(Desktop.Action.MAIL)) {
            showError("Not supported");
            return;
        }
 
        File fx = e.createMessage("Send CloudCoins", "CloudCoins", exportedFilenames);
        
        try {        
            desktop.open(fx);
            fx.delete();
        } catch (IOException ex) {
            System.out.println("Failed to open dialog");
            return;
        }
    }
    
   
    
  
    
    public void setCounters(int[][] counters) {
        this.counters = counters;
    }

    
  
        

    
    private void setRAIDAProgress(int raidaProcessed, int totalFilesProcessed, int totalFiles) {  
        currentImportStr = "Authenticated " + totalFilesProcessed + " of " + totalFiles + " CloudCoins" + System.lineSeparator();
        for (int i = 0; i < raidaProcessed; i++)
            currentImportStr += ".";
                    
        currentImportStr += System.lineSeparator();
    }
 


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
           for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                   // javax.swing.UIManager.setLookAndFeel(info.getClassName());
                   break;
                }
           }   
           UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
          
        } catch (InstantiationException ex) {
 
           
        } catch (IllegalAccessException ex) {
       
        } catch (javax.swing.UnsupportedLookAndFeelException ex) { 
      
        }
          
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Pbank();
            }
        });
    }
    
    class EchoCb implements CallbackInterface {
	public void callback(Object result) {
            if (!sm.getActiveWallet().isSkyWallet())
                sm.startFrackFixerService(new FrackFixerCb());
	}  
    }
    
    class ShowCoinsCb implements CallbackInterface {
	public void callback(final Object result) {
            final Object fresult = result;
            ShowCoinsResult scresult = (ShowCoinsResult) fresult;
                 
            cbState = CB_STATE_DONE;
            setCounters(scresult.counters);  
        }
    }
    
    class UnpackerCb implements CallbackInterface {
	public void callback(Object result) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {

            }
            
            importState = IMPORT_STATE_IMPORT;
            setRAIDAProgress(0, 0, AppCore.getFilesCount(Config.DIR_SUSPECT, currentWallet));
            sm.startAuthenticatorService(new AuthenticatorCb());
 
        }
    }

    class AuthenticatorCb implements CallbackInterface {
	public void callback(Object result) {
            final Object fresult = result;
	
            AuthenticatorResult ar = (AuthenticatorResult) fresult;

            if (ar.status == AuthenticatorResult.STATUS_ERROR) {
                importState = IMPORT_STATE_INIT;
                setScreen(SCREEN_MAIN_WALLET);
                setError("Import failed");
                return;
            } else if (ar.status == AuthenticatorResult.STATUS_FINISHED) {
                sm.startGraderService(new GraderCb());
                return;
            } else if (ar.status == AuthenticatorResult.STATUS_CANCELLED) {
                importState = IMPORT_STATE_INIT;
                setError("Cancelled");
                setScreen(SCREEN_MAIN_WALLET);
                return;
            }

            setRAIDAProgress(ar.totalRAIDAProcessed, ar.totalFilesProcessed, ar.totalFiles);
	}
    }
    
    class GraderCb implements CallbackInterface {
	public void callback(Object result) {
            GraderResult gr = (GraderResult) result;

            statToBankValue = gr.totalAuthenticValue + gr.totalFrackedValue;
            statToBank = gr.totalAuthentic + gr.totalFracked;
            statFailed = gr.totalLost + gr.totalCounterfeit + gr.totalUnchecked;

            sm.getActiveWallet().appendTransaction("Import", statToBankValue);
            
            if (!sm.getActiveWallet().isEncrypted()) {
                importState = IMPORT_STATE_DONE;
            } else {
                sm.startVaulterService(new VaulterCb());
            }       
	}
    }

    class FrackFixerCb implements CallbackInterface {
	public void callback(Object result) {
            FrackFixerResult fr = (FrackFixerResult) result;

            if (fr.status == FrackFixerResult.STATUS_ERROR) {
                wl.error(ltag, "Failed to fix");
		return;
            }

            if (fr.status == FrackFixerResult.STATUS_FINISHED) {
		if (fr.fixed + fr.failed > 0) {
                    wl.debug(ltag, "Fracker fixed: " + fr.fixed + ", failed: " + fr.failed);
                    return;
		}
            }
            
            sm.startLossFixerService(new LossFixerCb());
        }
    }
         
    class ExporterCb implements CallbackInterface {
	public void callback(Object result) {
            ExporterResult er = (ExporterResult) result;
            if (er.status == ExporterResult.STATUS_ERROR) {
                cbState = CB_STATE_DONE;
		setError("Failed to export");
		return;
            }

            if (er.status == ExporterResult.STATUS_FINISHED) {
		exportedFilenames = er.exportedFileNames;
                cbState = CB_STATE_DONE;
                
                sm.getActiveWallet().appendTransaction("Export", er.totalExported * -1);
		return;
            }
	}
    }
    
    class VaulterCb implements CallbackInterface {
	public void callback(final Object result) {
            final Object fresult = result;
            VaulterResult vresult = (VaulterResult) fresult;
            
            importState = IMPORT_STATE_DONE;
            cbState = CB_STATE_DONE;

	}
    }
    
    class LossFixerCb implements CallbackInterface {
	public void callback(final Object result) {
            LossFixerResult lr = (LossFixerResult) result;
            
            wl.debug(ltag, "LossFixer finished");
            sm.startEraserService(new EraserCb());
        }
    }
				
    class BackupperCb implements CallbackInterface {
	public void callback(final Object result) {
            BackupperResult br = (BackupperResult) result;
            
            wl.debug(ltag, "Backupper finished");
	}
    }

    class EraserCb implements CallbackInterface {
        public void callback(final Object result) {
            EraserResult er = (EraserResult) result;

            wl.debug(ltag, "Eraser finished");
	}
    }
    
    class SenderCb implements CallbackInterface {
	public void callback(Object result) {
            SenderResult sr = (SenderResult) result;
            
            wl.debug(ltag, "Sender finished: " + sr.status);
            cbState = CB_STATE_DONE;

            if (sr.amount > 0) {
                sm.getActiveWallet().appendTransaction(sr.memo, sr.amount * -1);
                Wallet dstWallet = sm.getWallet(currentDstWallet);
                if (dstWallet != null) {
                    dstWallet.appendTransaction(sr.memo, sr.amount);
                    if (dstWallet.isEncrypted()) {
                        wl.debug(ltag, "Set wallet to " + currentDstWallet);
                        sm.changeServantUser("Vaulter", currentDstWallet);
                        sm.startVaulterService(new VaulterCb());          
                    }
                }            
            }
   
            if (sr.status == SenderResult.STATUS_ERROR) {
		setError("Failed to send. Please check amount of coins in the Wallet");
		return;
            }        
	}
    }
    
    class ReceiverCb implements CallbackInterface {
	public void callback(Object result) {
            ReceiverResult sr = (ReceiverResult) result;
            
            wl.debug(ltag, "Receiver finished");
            
            currentWallet = currentDstWallet;
            if (!sm.getActiveWallet().isEncrypted()) {
                cbState = IMPORT_STATE_DONE;
            } else {
                sm.startVaulterService(new VaulterCb());
            }  
            
	}
    }
    
}