package pbank;

import global.cloudcoin.ccbank.Authenticator.Authenticator;
import global.cloudcoin.ccbank.Authenticator.AuthenticatorResult;
import global.cloudcoin.ccbank.Echoer.Echoer;
import global.cloudcoin.ccbank.Exporter.Exporter;
import global.cloudcoin.ccbank.Exporter.ExporterResult;
import global.cloudcoin.ccbank.FrackFixer.FrackFixer;
import global.cloudcoin.ccbank.FrackFixer.FrackFixerResult;
import global.cloudcoin.ccbank.Grader.Grader;
import global.cloudcoin.ccbank.Grader.GraderResult;
import global.cloudcoin.ccbank.ServantManager.ServantManager;
import global.cloudcoin.ccbank.ShowCoins.ShowCoins;
import global.cloudcoin.ccbank.ShowCoins.ShowCoinsResult;
import global.cloudcoin.ccbank.Unpacker.Unpacker;
import global.cloudcoin.ccbank.Vaulter.VaulterResult;
import global.cloudcoin.ccbank.core.AppCore;
import global.cloudcoin.ccbank.core.CallbackInterface;
import global.cloudcoin.ccbank.core.Config;
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


import pbank.ImageJPanel;

/**
 * 
 */
public class Pbank implements ActionListener, ComponentListener {

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

    
    int currentScreen;
    String currentWallet;
    String currentMessage;
    String currentError;
    
    String currentImportStr;
    
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
    
    AppUI appUI;    
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
        if (1==1)
        return;
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
        System.out.println("*** CloudCoin Console Client ***");
        System.out.println();
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
        
        showTitle("Create Sky Wallet. Enter the path of the ID Coin: ");
        showCursor();
        
        path = readItem();
        File f = new File(path);
        if (!f.exists()) {
            setError("File does not exist");
            setScreen(SCREEN_MAIN_WALLET);
            return;
        }
        
        if (!AppCore.moveToFolder(path, Config.DIR_ID, currentWallet)) {
            setError("Failed to move ID coin");
            setScreen(SCREEN_MAIN_WALLET);
            return;
        }
        
        showMessage("The SkyWallet has been created");
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
                String password = readItem();
                
                sm.getActiveWallet().setPassword(password);
            }
        }
        
        setScreen(SCREEN_MAIN_WALLET);
    }
    
    private void showWalletMainScreen() {    
        sm.startEchoService(new EchoCb());
        
        showTitle("Select action for wallet " + currentWallet);
        
        showBasicScreen(new String[] {"Deposit Coins", "Show Coins", 
            "Withdraw Coins", "Transfer Coins", "Show Transactions", "Create Sky Wallet", "Back"
        }, new int[] {  SCREEN_DEPOSIT, SCREEN_SHOW_COINS, SCREEN_WITHDRAW,
            SCREEN_TRANSFER, SCREEN_SHOW_TRANSACTIONS, SCREEN_CREATE_SKYWALLET, SCREEN_SELECT_WALLET 
        });      
    }
    
    private void showWalletTransactionsScreen() {
        showTitle("Transactions");
        
        Wallet w = sm.getActiveWallet();
        //w.appendTransaction("xxx", -1000);
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
        showTitle("Result of the Export");
        
        if (cbState != CB_STATE_DONE) {
            doSleep(200);
            return;
        }
        
        if (currentError == null || currentError.equals(""))
            showMessage("Exported successfully");
        
        cbState = CB_STATE_INIT;
        setScreen(SCREEN_MAIN_WALLET);
        
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
        appUI = new AppUI(tw, th);
    
        
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
                    
                    
            }
            /*
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                
            }*/
            
            if (1 == 0)
                break;
            
        }
  
            
       
            
        
        
        
        
        
        if (1==1)
            return;
        
        initSystem();
        
        ImageJPanel mainPanel = new ImageJPanel("resources/backicons.png");   
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setImageSize(tw);
        appUI.setSize(mainPanel, tw, th);
        appUI.align(mainPanel);
        mainPanel.setBackground(appUI.getOurColor());
        
        // Logo
        ImageJPanel i = new ImageJPanel(new FlowLayout(FlowLayout.CENTER), "resources/logo3.png");
        i.setImageSize((int) (0.55 * tw));
        appUI.align(i);
        appUI.setSize(i, i.getImageWidth(), i.getImageHeight());
        i.setOpaque(false);
        mainPanel.add(i);
        
        // Button1
        JButton mb = appUI.getMainButton("importbutton.png", "DEPOSIT");
        mb.addActionListener(this);
        mainPanel.add(appUI.hr(60));
        mainPanel.add(mb);
     
        // Button2
        mb = appUI.getMainButton("bankbutton.png", "BANK");
        mb.addActionListener(this);
        mainPanel.add(appUI.hr(45));
        mainPanel.add(mb);
     
        // Button3
        mb = appUI.getMainButton("exportbutton.png", "WITHDRAW");
        mb.addActionListener(this);
        mainPanel.add(appUI.hr(45));
        mainPanel.add(mb);
        
        // Text
        mainPanel.add(appUI.hr(45));
        mainPanel.add(appUI.getJLabel("CloudCoin Consortium"));
           
        // Version
        mainPanel.add(appUI.hr(15));
        mainPanel.add(appUI.getJLabel("Version 1.0"));
          
        // Main Frame
        mainFrame = appUI.getMainFrame();
        mainFrame.add(mainPanel, BorderLayout.CENTER);
         
        mainFrame.addComponentListener(this);
    }
    
    @Override
    public void componentHidden(ComponentEvent arg0) {
    }

    @Override
    public void componentMoved(ComponentEvent arg0) {
        if (xframeImport == null)
            return;
        
        int dx = xframeImport.getX();
        int dy = xframeImport.getY();
        
        int x = mainFrame.getX();
        int y = mainFrame.getY();

        xframeImport.setLocation(x - idx, y - idy);
    }

    @Override
    public void componentResized(ComponentEvent arg0) {
    }

    @Override
    public void componentShown(ComponentEvent arg0) {
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
    
    public void showExportResult() {
        xframeExport = appUI.getDialog(mainFrame, "Withdraw Coins", 160);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBackground(Color.WHITE);
        appUI.align(mainPanel);    
        
        JLabel j = appUI.getJLabel("Exported successfully", Font.BOLD, 16);
        mainPanel.add(j);
        mainPanel.add(appUI.hr(15));
        
        JButton jb = appUI.getCommonButton("EMAIL", "DoEmailResult");
        jb.addActionListener(this);
        
        mainPanel.add(jb);
        
        
        xframeExport.add(mainPanel);
        
        
        xframeExport.setVisible(true);
    }
    
    
    public void showExportScreen(int[][] counters) {
        Component j;
        
        j = getExportScreen(counters); 
        xframeExport = appUI.getDialog(mainFrame, "Withdraw Coins", 520);
        xframeExport.add(j);
        xframeExport.setVisible(true);
    }
    
    public void showBankScreen(int[][] counters) {
        Component j;
        
        
        xframeBank = appUI.getDialog(mainFrame, "Bank Inventory", 250);
   
        j = getBankScreen(counters);       
        
        xframeBank.add(j);
        xframeBank.setVisible(true);
    }
    
    public void showImportScreen(String user) {
        if (xframeImport != null) {
            xframeImport.setName("Autoclose");
            xframeImport.dispose();
        }
        
        int totalFiles = AppCore.getFilesCount(Config.DIR_IMPORT, user);
        
        int height = 340;
        if (importState == IMPORT_STATE_INIT) {
            if (totalFiles == 0) {
                currentImportComponent = getImportScreen(1);
                height = 260;
            } else {
                currentImportComponent = getImportScreen(2);
            }
        } else if (importState == IMPORT_STATE_UNPACKING) {
            currentImportComponent = getImportScreen(5);
            height = 120;
        } else if (importState == IMPORT_STATE_IMPORT) {
            currentImportComponent = getImportScreen(3);
            height = 220;
        } else if (importState == IMPORT_STATE_DONE) {
            currentImportComponent = getImportScreen(4);
        }
        
        xframeImport = appUI.getDialog(mainFrame, "Deposit coins", height);
        xframeImport.add(currentImportComponent);
        xframeImport.setModal(false);
        xframeImport.setName("ImportDialog");
        xframeImport.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        idx = mainFrame.getX() - xframeImport.getX();
        idy = mainFrame.getY() - xframeImport.getY();
        
        xframeImport.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (e.getComponent().getName() == "Autoclose")
                    return;
                
                importState = IMPORT_STATE_INIT;
                if (sr.isRunning("Authenticator")) {
                    at.cancel();
		}
            }
        });
        
        xframeImport.setVisible(true);
    }
    
   
    public void actionPerformed(ActionEvent e) { 
        String command = ((JButton) e.getSource()).getActionCommand();
        if (command.equals("BANK")) {
            requestedDialog = DIALOG_BANK;
            //startShowCoinsService();
            
        } else if (command.equals("DEPOSIT")) {        
            if (echoResult != ECHO_RESULT_DONE) {
                //appUI.showMessage(mainFrame, "RAIDA is being checked. Please wait...");
                showMessage("RAIDA is being checked");
                return;
            }
            
            requestedDialog = DIALOG_IMPORT;
            showImportScreen(Config.DIR_DEFAULT_USER);
        } else if (command.equals("DoImport")) {
            importState = IMPORT_STATE_UNPACKING;
            showImportScreen(Config.DIR_DEFAULT_USER);
            //startUnpackerService();
        } else if (command.equals("DoEmailReport")) {
            doEmailReceipt();
        } else if (command.equals("WITHDRAW")) {
            requestedDialog = DIALOG_EXPORT;
            //startShowCoinsService();
        } else if (command.equals("ExJPG")) {
            exportType = Config.TYPE_JPEG;
            exJpg.setBackground(appUI.getOurColor());
            exStack.setBackground(appUI.getDisabledColor());            
        } else if (command.equals("ExStack")) {
            exportType = Config.TYPE_STACK;
            exJpg.setBackground(appUI.getDisabledColor());
            exStack.setBackground(appUI.getOurColor());
        } else if (command.equals("DoWithdraw")) {
            doWithdraw();
        } else if (command.equals("DoEmailResult")) {
            doEmailResult();
        }
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
    
    public void doWithdraw() {
        String tag = exportTextField.getText();
        int[] values;
        
        values = new int[AppCore.getDenominations().length];
        for (int i = 0; i < AppCore.getDenominations().length; i++) {
            JSpinner s = (JSpinner) appUI.getByName(xframeExport, "sp" + AppCore.getDenominations()[i]);
            if (s == null) {
                System.out.println("Internal error");
                return;
            }
            
            try {
                s.commitEdit();
            } catch ( java.text.ParseException e ) { }
            
            values[i] = (Integer) s.getValue();
        }
        
        Exporter ex = (Exporter) sr.getServant("Exporter");
//	ex.launch(Config.DIR_DEFAULT_USER, exportType, values, tag, new ExporterCb());
    }
    
    public Component getExportScreen(int[][] counters) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBackground(Color.WHITE);
        appUI.align(mainPanel);    
       
        // Title
        JLabel j = appUI.getJLabel("Withdraw Coins", Font.BOLD, 16);
        mainPanel.add(j);
        mainPanel.add(appUI.hr(15));
        
        // Coins amount
        int totalCnt = AppCore.getTotal(counters[Config.IDX_FOLDER_BANK]) +
				AppCore.getTotal(counters[Config.IDX_FOLDER_FRACKED]);
        j = appUI.getJLabel("" + totalCnt, Font.BOLD, 14);
        j.setForeground(appUI.getOurColor());
        mainPanel.add(j);
        
        // The phrase 
        j = appUI.getJLabel("Total coins in Bank", Font.PLAIN, 12);
        mainPanel.add(j);
        
        // jpg or stack
        JPanel optPanel = new JPanel();
        optPanel.setLayout(new GridLayout(1,2));
        appUI.setSize(optPanel, tw/4, 30);
        
        mainPanel.add(appUI.hr(15));
        
        // Button JPG
        exJpg = appUI.getSwitchButton("jpeg", "ExJPG", appUI.getDisabledColor());
        exJpg.addActionListener(this);
        optPanel.add(exJpg);
        
        // Button Stack
        exStack = appUI.getSwitchButton("stack", "ExStack", appUI.getOurColor());
        exStack.addActionListener(this);
        optPanel.add(exStack);
        
        mainPanel.add(optPanel);
   
        JPanel xPanel = new JPanel();
        xPanel.setLayout(new FlowLayout());
        xPanel.setBackground(Color.WHITE);
        appUI.setSize(xPanel, tw/2, 100);      
        
        JPanel s;
        for (int i = 0; i < AppCore.getDenominations().length; i++) {
            int authCount = counters[Config.IDX_FOLDER_BANK][i] +
		counters[Config.IDX_FOLDER_FRACKED][i];
            
            s = (JPanel) appUI.getEDenomPart(authCount, AppCore.getDenominations()[i]);
            xPanel.add(s);
        }
        
        mainPanel.add(appUI.hr(25));
        mainPanel.add(xPanel);
        
        JLabel tag = appUI.getJLabel("Export Tag (Optional)", Font.PLAIN, 10);
        mainPanel.add(tag);
        
        exportTextField = new JTextField(10);
        appUI.setSize(exportTextField, tw/4, 32);
        
        mainPanel.add(appUI.hr(10));
        mainPanel.add(exportTextField);
        
        mainPanel.add(appUI.hr(25));
        JLabel message = appUI.getJLabel("<html><p>When you withdraw your CloudCoins they will be placed in the Export folder."
             + " If you email the CloudCoins they will be removed from the Export folder and "
                + " sent by email.</p></html>", Font.PLAIN, 10);
        mainPanel.add(message);
          
        JButton jb = appUI.getCommonButton("WITHDRAW", "DoWithdraw");
        jb.addActionListener(this);
        
        mainPanel.add(appUI.hr(25));
        mainPanel.add(jb);
        
        return mainPanel;     
    }
    
    public void setCounters(int[][] counters) {
        this.counters = counters;
    }
    
    public Component getBankScreen(int[][] counters) {
        JLabel j;
        JPanel mainPanel;
        
        // Main Panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBackground(Color.WHITE);
        appUI.align(mainPanel);
        
        // Title
        j = appUI.getJLabel("Bank Inventory", Font.BOLD, 16);
        mainPanel.add(j);
  
        // Coins
        int totalCnt = AppCore.getTotal(counters[Config.IDX_FOLDER_BANK]) +
				AppCore.getTotal(counters[Config.IDX_FOLDER_FRACKED]);
        j = appUI.getJLabel("Total authentic coins: " + totalCnt, Font.PLAIN, 12);
        mainPanel.add(appUI.hr(25));
        mainPanel.add(j);

        // Main Holder        
        JPanel xPanel = new JPanel();
        xPanel.setLayout(new FlowLayout());
        xPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        xPanel.setBackground(Color.WHITE);
             
        JPanel s;

        for (int i = 0; i < AppCore.getDenominations().length; i++) {
            int authCount = counters[Config.IDX_FOLDER_BANK][i] +
		counters[Config.IDX_FOLDER_FRACKED][i];
            
            s = (JPanel) appUI.getDenomPart(authCount, AppCore.getDenominations()[i]);
            xPanel.add(s);
        }
        
        mainPanel.add(appUI.hr(25));
        mainPanel.add(xPanel);

        return mainPanel;
    }
    
    public Component getImportScreen(int screenNum) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBackground(Color.WHITE);
        appUI.align(mainPanel);
        
        JLabel j = appUI.getJLabel("Deposit Coins", Font.BOLD, 16);
        mainPanel.add(j);
        mainPanel.add(appUI.hr(25));
        
        Component c;
        if (screenNum == 1)
            c = showImportScreenNoCoins(Config.DIR_DEFAULT_USER);
        else if (screenNum == 2)
            c = showImportScreenGetReady(Config.DIR_DEFAULT_USER);
        else if (screenNum == 3)
            c = showImportScreenDoing(Config.DIR_DEFAULT_USER);
        else if (screenNum == 4)
            c = showImportScreenDone();
        else if (screenNum == 5)
            c = showImportScreenUnpacking();
        else
            c = null;
        
        mainPanel.add(c);
        
        return mainPanel;
    }
    
    public Component showImportScreenUnpacking() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBackground(Color.WHITE); 

        JLabel j = appUI.getJLabel("Unpacking...", Font.PLAIN, 12);
        mainPanel.add(j);
        
        return mainPanel;
    }
        
    public Component showImportScreenNoCoins(String user) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBackground(Color.WHITE); 
        
        String importDir = AppCore.getUserDir(Config.DIR_IMPORT, user);
	
        
        JLabel j = appUI.getJLabel("<html><p align='center'>There were no CloudCoins found<br>in your Import folder. "
                + "Please put your CloudCoins (.jpg or .stack) in your "
                + "Import folder and try again. <br><br>Your Import folder is: </p><br></html>", Font.PLAIN, 12);

        mainPanel.add(j);
        
        JTextArea ja = appUI.getFolderTA(importDir);
        mainPanel.add(ja);
        
        return mainPanel;
    }
    
    public Component showImportScreenGetReady(String user) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBackground(Color.WHITE);
    
        JLabel j = appUI.getJLabel("<html><p align='center'>Deposit will import all CloudCoin files from your Import folder. "
                + "Please put your CloudCoins (.jpg or .stack) in your "
                + "Import folder and try again. <br><br>Your Import folder is: </p><br></html>", Font.PLAIN, 12);

        mainPanel.add(j);
        
        JTextArea ja = appUI.getFolderTA("c:\\documents\\sasdfas\\fsafas\\dsadasdas\\dasdasdas\\file.txt");
        mainPanel.add(ja);
        
        int total = AppCore.getFilesCount(Config.DIR_IMPORT, user);
        j = appUI.getJLabel("<html><p align='center'>We are going to import " + 
                total + " file(s). Click OK to continue</p></html>", Font.PLAIN, 12);
        mainPanel.add(j);
        
        JButton jb = appUI.getCommonButton("OK", "DoImport");
        jb.addActionListener(this);
    
        mainPanel.add(appUI.hr(10));
        mainPanel.add(jb);

        return mainPanel;
    }
    
    private void setRAIDAProgress(int raidaProcessed, int totalFilesProcessed, int totalFiles) {  
        currentImportStr = "Authenticated " + totalFilesProcessed + " of " + totalFiles + " CloudCoins" + System.lineSeparator();
        for (int i = 0; i < raidaProcessed; i++)
            currentImportStr += ".";
                    
        currentImportStr += System.lineSeparator();
    }
 
    public Component showImportScreenDoing(String user) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBackground(Color.WHITE);
  
        jraida = appUI.getJLabel("", Font.PLAIN, 12);
       
        mainPanel.add(jraida);
  
        UIManager.put("ProgressBar.selectionForeground", Color.BLACK);
        UIManager.put("ProgressBar.foreground", appUI.getOurColor());
        pbar = new JProgressBar();
        appUI.setSize(pbar, 320, 30);
        pbar.setStringPainted(true);
        pbar.setBackground(Color.WHITE);
        pbar.setMinimum(0);
        pbar.setMaximum(24);
        pbar.setValue(0);

        mainPanel.add(appUI.hr(25));
        mainPanel.add(pbar);
        mainPanel.add(appUI.hr(45));

        return mainPanel;
    }
    
    
    public Component showImportScreenDone() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBackground(Color.WHITE);

        JLabel j = appUI.getJLabel("" + statToBankValue, Font.BOLD, 16);
        j.setForeground(appUI.getOurColor());
        mainPanel.add(j);
        
        j = appUI.getJLabel("Total Coins moved to Bank", Font.PLAIN, 11);
        mainPanel.add(j);
        mainPanel.add(appUI.hr(25));
        
        // Canvas dashed line
        MyCanvas s = new MyCanvas();
        s.setBackground(Color.RED);
        appUI.setSize(s, 350, 10);
        mainPanel.add(s);
        mainPanel.add(appUI.hr(25));
     
        // Horizontal container
        JPanel resPanel = new JPanel();
        resPanel.setLayout(new BoxLayout(resPanel, BoxLayout.LINE_AXIS));
        resPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        resPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resPanel.setBackground(Color.WHITE);
        
        // Authed Left Pane
        JPanel authedPanel = new JPanel();
        authedPanel.setLayout(new BoxLayout(authedPanel, BoxLayout.PAGE_AXIS));
        authedPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        authedPanel.setMaximumSize(new Dimension(tw/2, th));
        authedPanel.setPreferredSize(new Dimension(tw/2, th/2));
        authedPanel.setMinimumSize(new Dimension(tw/4, 10));
        authedPanel.setBackground(Color.WHITE);
 
        j = appUI.getJLabel("" + statToBank, Font.BOLD, 14);
        authedPanel.add(j);
        authedPanel.add(appUI.hr(15));
        
        j = appUI.getJLabel("Authentic", Font.PLAIN, 9);
        j.setForeground(appUI.getDisabledColor());
        authedPanel.add(j);

        j = appUI.getJLabel("and moved to Bank", Font.PLAIN, 9);
        j.setForeground(appUI.getDisabledColor());
        authedPanel.add(j);

        // Counterfeit Right Pane
        JPanel counterfeitPanel = new JPanel();
        counterfeitPanel.setLayout(new BoxLayout(counterfeitPanel, BoxLayout.PAGE_AXIS));
        counterfeitPanel.setBackground(Color.WHITE);
        counterfeitPanel.setMaximumSize(new Dimension(tw/2, th));
        counterfeitPanel.setPreferredSize(new Dimension(tw/2, th/2));
        counterfeitPanel.setMinimumSize(new Dimension(tw/4, 10));
        
        j = appUI.getJLabel("" + statFailed, Font.BOLD, 14);
        j.setForeground(new Color(0x777777));        
        counterfeitPanel.add(j);
        counterfeitPanel.add(appUI.hr(15));
        
        j = appUI.getJLabel("Counterfeit or failed", Font.PLAIN, 9);
        j.setForeground(new Color(0x777777));
        counterfeitPanel.add(j);
        
        j = appUI.getJLabel("and moved to Trash", Font.PLAIN, 9);
        j.setForeground(new Color(0x777777));
        counterfeitPanel.add(j);
        
        resPanel.add(authedPanel);  
        resPanel.add(counterfeitPanel);  
        mainPanel.add(resPanel);
        
        JButton jb = appUI.getCommonButton("EMAIL", "DoEmailReport");
        jb.addActionListener(this);
        
        mainPanel.add(jb);
        
        return mainPanel;    
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
            System.out.println("Launch fracker");
            sm.startFrackFixerService(new FrackFixerCb());
	}  
    }
    
    class ShowCoinsCb implements CallbackInterface {
	public void callback(final Object result) {
            final Object fresult = result;
            ShowCoinsResult scresult = (ShowCoinsResult) fresult;
                 
            cbState = CB_STATE_DONE;
            if (requestedDialog == DIALOG_BANK)
                setCounters(scresult.counters);
                //showBankScreen(scresult.counters);
            else if (requestedDialog == DIALOG_EXPORT) {
                //showExportScreen(scresult.counters);
            }
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

            System.out.println("xxx="+ ar.totalFiles);
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
		//showError("Failed to fix");
		return;
            }

            if (fr.status == FrackFixerResult.STATUS_FINISHED) {
		if (fr.fixed + fr.failed > 0) {
                   // showMessage("Fracker fixed: " + fr.fixed + ", failed: " + fr.failed);
                    return;
		}
            }
        }
    }
         
    class ExporterCb implements CallbackInterface {
	public void callback(Object result) {
            ExporterResult er = (ExporterResult) result;
            if (er.status == ExporterResult.STATUS_ERROR) {
		setError("Failed to export");
		return;
            }

            if (er.status == ExporterResult.STATUS_FINISHED) {
		exportedFilenames = er.exportedFileNames;
                cbState = CB_STATE_DONE;
                
                sm.getActiveWallet().appendTransaction("Export", er.totalExported * -1);
                //xframeExport.dispose();
		//showExportResult();
		return;
            }
	}
    }
    
    class VaulterCb implements CallbackInterface {
	public void callback(final Object result) {
            final Object fresult = result;
            VaulterResult vresult = (VaulterResult) fresult;
            
            importState = IMPORT_STATE_DONE;

	}
    }

}