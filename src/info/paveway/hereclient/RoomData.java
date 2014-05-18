package info.paveway.hereclient;

import java.util.Date;

public class RoomData {

    private long mRoomNo;
    private boolean mUsed;
    private String mPassword;
    private String mUserId;
    private String mNickname;
    private Date mUpdate;

    public void setRoomNo(long roomNo)       { this.mRoomNo   = roomNo;   }
    public void setUsed(boolean used)        { this.mUsed     = used;     }
    public void setPassword(String password) { this.mPassword = password; }
    public void setUserId(String userId)     { this.mUserId   = userId;   }
    public void setNickname(String nickname) { this.mNickname = nickname; }
    public void setUpdate(Date update)       { this.mUpdate   = update;   }
    public Long getRoomNo()     { return mRoomNo;   }
    public boolean getUsed()    { return mUsed;     }
    public String getPassword() { return mPassword; }
    public String getUserId()   { return mUserId;   }
    public String getNickname() { return mNickname; }
    public Date getUpdate()     { return mUpdate;   }
}
