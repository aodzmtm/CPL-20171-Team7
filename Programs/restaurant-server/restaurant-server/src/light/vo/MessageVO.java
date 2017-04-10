package light.vo;

import com.google.gson.Gson;

public class MessageVO {
 
    private String message;
    private String type;
    private String to;
     
    // ����ڰ� ���� �޼��� ������ ��� ��Ȱ�� �Ѵ�.
    public static MessageVO convertMessage( String source ) {
        MessageVO message = new MessageVO();
        Gson gson = new Gson();
        // ����ڰ� ���� json Ÿ���� ������ �ڹ� ��ü�� �־��ش�.
        message = gson.fromJson(source, MessageVO.class);
         
        return message;
    }
     
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }
}