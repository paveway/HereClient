package info.paveway.hereclient;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MacAddress {

    private static final String TAG = MacAddress.class.getSimpleName();
    private static StringBuilder mStringBuilder = new StringBuilder();

    public static String getMacAddressString() {

        Map<String, String> macs = getMacAddress();

        mStringBuilder.setLength(0);
        for (Entry<String, String> e : macs.entrySet()) {

            mStringBuilder.append("intf name:" + e.getKey());
            mStringBuilder.append(", mac:" + e.getValue());
            mStringBuilder.append("\n");
        }
        return mStringBuilder.toString();
    }

    public static String getMacAddressString(String interfaceName) {
        Map<String, String> macAddressMap = getMacAddress();
        return macAddressMap.get(interfaceName);
    }

    public static Map<String, String> getMacAddress() {
        HashMap<String, String> macs = new HashMap<String, String>();
        try {
            List<NetworkInterface> interfaces =
                    Collections.list(NetworkInterface.getNetworkInterfaces());

            for (NetworkInterface intf : interfaces) {
                String name = intf.getName();
                if (null != name) {
                    byte[] raw = intf.getHardwareAddress();
                    if (null != raw) {
                        String mac = getMacString(raw);
                        macs.put(name, mac);
                    } else {
                        macs.put(name, null);
                    }
                }
            }
        } catch(Exception e) {
            return null;
        }
        return macs;
    }

    private static String getMacString(byte[] raw) {
        mStringBuilder.setLength(0);
        for (int i = 0; i < raw.length; i++) {
            mStringBuilder.append(String.format("%02x", raw[i]));
        }
        return mStringBuilder.toString();
    }
}
