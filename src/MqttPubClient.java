import org.eclipse.paho.client.mqttv3.*;

public class MqttPubClient{
    // MQTT통신에서 publisher와 Subscriber의 역할을 하기 위한 기능을 가진 객체
    private MqttClient mqttClient;
    public MqttPubClient(){
        try {
            mqttClient = new MqttClient("tcp://192.168.0.180:1883", "user1"); // broker 서버
            mqttClient.connect();   // broker 접속
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    // 메세지 전송을 위한 메소드
    public boolean send(String topic, String msg){
        try {
            // broker로 전송할 메세지 생성 - MqttMessage
            MqttMessage message = new MqttMessage();
            message.setPayload(msg.getBytes()); // 실제 broker로 전송할 메세지
            mqttClient.publish(topic,message);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return true;
    }
    // 종료
    public void close(){
        if(mqttClient != null){
            try {
                mqttClient.disconnect();
                mqttClient.close();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        MqttPubClient sender = new MqttPubClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 1;
                String msg ="";
                while (true){
                    if(i == 5) break;
                    else {
                        if(i % 2 == 1) msg = "led_on";
                        else msg= "led_off";
                    }
                    sender.send("test",msg);
                    i++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sender.send("test2","임의 값");
                }
                sender.close();     // 작업 완료되면 종료하기
            }
        }).start();

    }

}
