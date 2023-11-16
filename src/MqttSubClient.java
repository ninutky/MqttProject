import org.eclipse.paho.client.mqttv3.*;

public class MqttSubClient implements MqttCallback {
    private MqttClient mqttClient;
    // Mqtt프로토콜를 이용해서 broker에 연결하면서 연결정보를 설정할 수 있는 객체
    private MqttConnectOptions mqttOptions;

    // clientId는 broker가 클라이언트를 식별하기 위한 문자열 - 고유
    public MqttSubClient init(String server, String clientId){
        try {
            mqttOptions = new MqttConnectOptions();
            mqttOptions.setCleanSession(true);
            mqttOptions.setKeepAliveInterval(30);
            // broker의 subscriber 하기 위한 클라이언트 객체 생성
            mqttClient = new MqttClient(server, clientId);
            // 클라이언트 객체에 Mqttcallback을 등록- 구독신청 후 적절한 시점에 처리하고 싶은 기능을 구현하고
            // 메소드가 자동으로 그 시점에 호출되도록 할 수 있다.
            mqttClient.setCallback(this);
            mqttClient.connect(mqttOptions);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return this;
    }

    // 커넥션이 종료되면 호출 - 통신오류로 연결이 끊어지는 경우 호출
    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Connection lost: " + throwable.getMessage());
    }

    // 메세지의 배달이 완료되면 호출
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        try {
            System.out.println("=====================메세지 도착=================");
            System.out.println("topic: " + topic + ", id: " + message.getId() + ", payload: " + new String(message.getPayload()));
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    // 메세지가 도착하면 호출되는 메소드드
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println("메세지 전송 완료");
    }

    // 구독 신청
    public boolean subscriber(String topic){
        boolean result = true ;
        try {
            if(topic!=null){
                // opic과 Qos를 전달
                // Qos는 메세지가 도착하기 위한 품질에 값을 설정 - 서비스 품질
                // 0,1,2를 설정할 수 있음
                mqttClient.subscribe(topic, 0);
            }
        } catch (MqttException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    public static void main(String[] args) {
        MqttSubClient subobj = new MqttSubClient();
        // broker 서버 호출
        subobj.init("tcp://192.168.0.180:1883","user2").subscriber("#"); // clientId는 중복되면 안됨
    }
}
