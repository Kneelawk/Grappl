package io.grappl.client.gui;

import io.grappl.GrapplGlobals;
import io.grappl.client.Application;
import io.grappl.client.ClientLog;
import io.grappl.client.GrapplDataFile;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.GrapplBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.URI;

/**
 * The standard Grappl gui.
 */
public class StandardGUI {

    private final String COMMAND_BUTTON_TEXT = "...";

    private JFrame jFrame;
    private Grappl grappl;
    private boolean isActuallyHash = false;
    private ConsoleWindow theConsoleWindow;
    private JLabel connectedClientsLabel;

    public StandardGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        jFrame = new JFrame("Grappl " + GrapplGlobals.VERSION);
        jFrame.setSize(new Dimension(310, 240));
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
        jFrame.setLayout(null);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JLabel usernameLable = new JLabel("Username");
        usernameLable.setBounds(5, 2, 250, 20);
        jFrame.add(usernameLable);
        final JTextField usernamef = new JTextField("");
        usernamef.setBounds(5, 22, 250, 20);
        usernamef.setText(GrapplDataFile.getUsername());
        jFrame.add(usernamef);

        final JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(5, 42, 250, 20);
        jFrame.add(passwordLabel);
        final JPasswordField jPasswordField = new JPasswordField("");
        jPasswordField.setBounds(5, 62, 250, 20);
        jPasswordField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                GrapplGlobals.usingSavedHashPass = false;
            }

            @Override
            public void keyPressed(KeyEvent e) {
                GrapplGlobals.usingSavedHashPass = false;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                GrapplGlobals.usingSavedHashPass = false;
            }
        });
        String password = GrapplDataFile.getPassword();
        jFrame.add(jPasswordField);
        if(password != null) {
            jPasswordField.setText(password);
            isActuallyHash = true;
        } else {
            Application.getClientLog().log("Password is null");
        }

        connectedClientsLabel = new JLabel("Waiting for connections");

        final JCheckBox rememberMeBox = new JCheckBox();
        rememberMeBox.setBounds(10, 87, 20, 20);
        jFrame.add(rememberMeBox);
        final JLabel rememberMeLabel = new JLabel("Remember me");
        rememberMeLabel.setBounds(35, 87, 250, 20);
        jFrame.add(rememberMeLabel);

        final StandardGUI theGUI = this;
        if(isActuallyHash) {
            rememberMeBox.setSelected(true);
        } {
            final JButton logInButton = new JButton("Log in");
            logInButton.setBounds(4, 112, 140, 40);
            logInButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    login(usernamef, jPasswordField, theGUI, rememberMeBox);
                }
            });
            jFrame.add(logInButton);

            JButton signUpButton = new JButton("Sign up");
            signUpButton.setBounds(148, 112, 140, 40);
            signUpButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        Desktop.getDesktop().browse(URI.create("http://grappl.io/register"));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            jFrame.add(signUpButton);

            JButton beAnonymousButton = new JButton("Run without logging in");
            beAnonymousButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jFrame.setVisible(false);
                    int wX = jFrame.getX();
                    int wY = jFrame.getY();

                    jFrame = new JFrame(GrapplGlobals.APP_NAME + " ");
                    jFrame.setSize(new Dimension(300, 240));
                    jFrame.setLocation(wX, wY);
                    jFrame.setVisible(true);
                    jFrame.setLayout(null);
                    jFrame.setResizable(false);
                    jFrame.setSize(new Dimension(290, 230));
                    jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    jFrame.setIconImage(GrapplGlobals.getIcon());

                    JButton consoleButton = new JButton(COMMAND_BUTTON_TEXT);
                    consoleButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (theConsoleWindow == null) {
                                theConsoleWindow = new ConsoleWindow(grappl);
                            } else {
                                theConsoleWindow.getTheFrame().toFront();
                            }
                        }
                    });
                    consoleButton.setBounds(235, 40, 40, 40);
                    jFrame.add(consoleButton);

                    grappl = new GrapplBuilder().login("default", ("1".hashCode() + "").toCharArray(), jFrame).withGUI(theGUI).build();

                    JButton jButton = new JButton("Close " + GrapplGlobals.APP_NAME + " Client");
                    jButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            System.exit(0);
                        }
                    });
                    jFrame.add(jButton);
                    jButton.setBounds(2, 105, 280, 90);

                    String ports = JOptionPane.showInputDialog("What port does your server run on?");
                    grappl.setInternalPort(Integer.parseInt(ports));
                    grappl.connect(grappl.getAuthentication().getLocalizedRelayPrefix() + "." + GrapplGlobals.DOMAIN);
                }
            });
            beAnonymousButton.setBounds(4, 155, 192, 40);
            jFrame.add(beAnonymousButton);

            JButton advancedButton = new JButton("Advanced");
            advancedButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jFrame.setVisible(false);
                    new AdvancedGUI().create();
                }
            });
            advancedButton.setBounds(200, 155, 90, 40);
            jFrame.add(advancedButton);

            jFrame.setIconImage(GrapplGlobals.getIcon());
            jFrame.repaint();
        }
    }

    public JLabel getConnectedClientsLabel() {
        return connectedClientsLabel;
    }

    public void initializeGUI(String relayServerIP, String publicPort, int localPort) {
        final JTextPane label = new JTextPane();
        label.setContentType("text");
        label.setText("Public address: " + relayServerIP + ":" + publicPort);
        label.setBorder(null);
        label.setBackground(null);
        label.setEditable(false);
        label.setBounds(5, 8, 450, 20);
        getjFrame().add(label);

        JLabel jLabel2 = new JLabel("Server on local port: " + localPort);
        jLabel2.setBounds(5, 25, 450, 20);
        getjFrame().add(jLabel2);

        final JLabel jLabel4 = new JLabel("Waiting for data");
        jLabel4.setBounds(5, 65, 450, 20);
        getjFrame().add(jLabel4);

        getjFrame().repaint();

        /* GUI update thread */
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (jLabel4 != null && connectedClientsLabel != null) {
                        connectedClientsLabel.setText("Connected clients: " + grappl.getStatMonitor().getOpenConnections());
                        jLabel4.setText("Sent Data: " + grappl.getStatMonitor().getSentDataKB() + "KB - Recv Data: " + grappl.getStatMonitor().getReceivedKB() + "KB");
                        getjFrame().repaint();
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public JFrame getjFrame() {
        return jFrame;
    }

    public void login(JTextField usernamef, JPasswordField jPasswordField, StandardGUI theGUI, JCheckBox rememberMeBox) {

        GrapplBuilder grapplBuilder = new GrapplBuilder();

        String username = usernamef.getText().toLowerCase();
        char[] password = jPasswordField.getPassword();

        try {
            if(!isActuallyHash || !GrapplGlobals.usingSavedHashPass) {
                password = (new String(password).hashCode() + "").toCharArray();
            }

            grapplBuilder.login(username, password, jFrame).withGUI(theGUI);
            grappl = grapplBuilder.build();

            if(grappl.getAuthentication().isLoggedIn()) {
                Application.getClientLog().log("Logged in as " + grappl.getUsername());
                Application.getClientLog().log("Alpha tester: " + grappl.getAuthentication().isPremium());
                Application.getClientLog().log("Static port: " + grappl.getExternalPort());

                if(!rememberMeBox.isSelected()) {
                    password = null;
                }

                GrapplDataFile.saveUsername(grappl.getUsername(), password);

                // options: nyc. sf. pac. lon. deu.
                String prefix = grappl.getAuthentication().getLocalizedRelayPrefix();

                String domain = prefix + "." + GrapplGlobals.DOMAIN;

                int wX = jFrame.getX();
                int wY = jFrame.getY();

                jFrame.setVisible(false);

                JFrame newJframe = new JFrame(GrapplGlobals.APP_NAME + " Client ("+ grappl.getUsername() + ")");
                // 300, 240
                newJframe.setSize(new Dimension(300, 240));
                newJframe.setLocation(wX, wY);
                newJframe.setResizable(false);
                newJframe.setSize(new Dimension(290, 230));
                newJframe.setIconImage(GrapplGlobals.getIcon());
                newJframe.setVisible(true);
                newJframe.setLayout(null);
                newJframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

                JButton jButton = new JButton("Close " + GrapplGlobals.APP_NAME + " Client");
                jButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                });
                newJframe.add(jButton);

                jButton.setBounds(2, 105, 280, 90);

                JButton consoleButton = new JButton(COMMAND_BUTTON_TEXT);
                consoleButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(theConsoleWindow == null) {
                            theConsoleWindow = new ConsoleWindow(grappl);
                        } else {
                            theConsoleWindow.getTheFrame().toFront();
                        }
                    }
                });
                consoleButton.setBounds(235, 40, 40, 40);
                newJframe.add(consoleButton);

                String ports = JOptionPane.showInputDialog("What port does your server run on?");

                try {
                    Thread.sleep(330);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                jFrame = newJframe;

                grappl.setInternalPort(Integer.parseInt(ports));
                try {
                    grappl.connect(domain);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(getjFrame(), "The value you entered is not a number");
                }
            } else {
                Application.getClientLog().log("Login failed!");
            }
        } catch (Exception esdfe) {
            Application.getClientLog().log("Yeah... that shouldn't have happened. Type the darn port next time!");
        }
    }

    public void destroyConsoleWindow() {
        theConsoleWindow = null;
    }
}
