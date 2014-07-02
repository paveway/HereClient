package info.paveway.hereclient.data;

import java.util.ArrayList;
import java.util.List;

/**
 * ここにいるクライアント
 * ルームデータクラス
 *
 * @version 1.0 新規作成
 *
 */
public class RoomData extends AbstractBaseData {

    /** オーナーID */
    private long mOwnerId;

    /** オーナー名 */
    private String mOwnerName;

    /** ユーザデータリスト */
    private List<UserData> mUserDataList = new ArrayList<UserData>();

    /**
     * オーナーIDを設定する。
     *
     * @param ownerId オーナーID
     */
    public void setOwnerId(long ownerId) {
        mOwnerId = ownerId;
    }

    /**
     * オーナーIDを返却する。
     *
     * @return オーナーID
     */
    public long getOwnerId() {
        return mOwnerId;
    }

    /**
     * オーナー名を設定する。
     *
     * @param ownerName オーナー名
     */
    public void setOwnerName(String ownerName) {
        mOwnerName = ownerName;
    }

    /**
     * オーナー名を返却する。
     *
     * @return オーナー名
     */
    public String getOwnerName() {
        return mOwnerName;
    }

    /**
     * ユーザデータを追加する。
     *
     * @param userData ユーザデータ
     */
    public void addUserData(UserData userData) {
        mUserDataList.add(userData);
    }

    /**
     * ユーザデータリストを返却する。
     *
     * @return ユーザデータリスト
     */
    public List<UserData> getUserDataList() {
        return mUserDataList;
    }
}
