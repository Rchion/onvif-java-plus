package be.teletask.onvif;

import be.teletask.onvif.listeners.OnvifResponseListener;
import be.teletask.onvif.models.OnvifDevice;
import be.teletask.onvif.models.OnvifMediaProfile;
import be.teletask.onvif.models.OnvifType;
import be.teletask.onvif.responses.OnvifResponse;
import be.teletask.onvif.util.OnvifResponsesAnalyzeUtils;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @author BOj
 * @desc
 * @since 11/12/2024  10:21 AM
 */

public class PtzTest {

    private final static String USER_NAME = "admin";
    private final static String PASSWORD = "a1234567";

    private final static String IP = "192.168.0.136";

    public static OnvifManager ONVIF_MANGER = new OnvifManager();

    public static OnvifDevice ONVIF_DEVICE = null;

    static {
        ONVIF_DEVICE = new be.teletask.onvif.models.OnvifDevice(IP, USER_NAME, PASSWORD);

        ONVIF_MANGER.setOnvifResponseListener(new OnvifResponseListener() {
            // 请求成功处理
            @Override
            public void onResponse(be.teletask.onvif.models.OnvifDevice onvifDevice, OnvifResponse onvifResponse) {
                System.out.println("onvifResponse = " + onvifResponse.getXml());
                OnvifType requestType = onvifResponse.getRequestType();
                switch (requestType) {
                    case GET_EVENTS_PULL_MESSAGE_URI:
                        // 此处获取了本次事件的所有data数据标签下的所有值
                        Map<String, String> pullMessageValues = OnvifResponsesAnalyzeUtils.getPullMessageValues(onvifResponse.getXml());

                        String isMotion = pullMessageValues.get("IsMotion");
                        if (Boolean.parseBoolean(isMotion)) {
                            System.out.println("\n有人经过");
                        } else {
                            System.out.println("\n无人经过");
                        }
                        break;
                    case GET_EVENTS_URI:
                        System.out.println("onvifResponse = " + onvifResponse.getXml());
                        break;
                    default:
                        System.out.println("onvifResponse = " + onvifResponse.getXml());
                        break;
                }
            }

            //请求失败处理
            @Override
            public void onError(be.teletask.onvif.models.OnvifDevice onvifDevice, int i, String s) {
                System.out.println("error = " + s);
                System.out.println(("失败"));
            }
        });
    }

    @Test
    public void ptzMove() throws Exception {
        List<OnvifMediaProfile> mediaProfiles = ONVIF_MANGER.getMediaProfiles(ONVIF_DEVICE);
        String token = mediaProfiles.get(0).getToken();
        ONVIF_MANGER.sendMoveRequestAndBody(ONVIF_DEVICE,token,0,0,1);
        while (true){
            Thread.sleep(2);
            ONVIF_MANGER.sendStopRequest(ONVIF_DEVICE,token,true,true);
        }
    }
}
