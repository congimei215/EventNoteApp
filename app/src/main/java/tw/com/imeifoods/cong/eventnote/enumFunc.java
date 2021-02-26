package tw.com.imeifoods.cong.eventnote;

/**
 * Created by user on 2017/9/8.
 */
public  class  enumFunc {
    public static final int Nan = 0;
    public static final int DayList = 1;
    public static final int DepartmentList = 2;
    public static final int TypeList = 3;
    public static final int LocationList = 4;
    public static final int EventList = 5;
    public static final int CAMERA= 6;
    public static final int AbnList = 7;
    public static final int SugList = 8;
    public static final int Setting = 9;
    public static final int WorkNoteList = 10;
    public static final int WorkItemList = 11;
    public static final int WorkItemStatusList  =12;
    public static final int AbnTypeList = 13;
}
/*
public  enum enumFunc {
    DayList(1), DepartmentList(2), TypeList(3), LocationList(4), EventList(5), CAMERA(6), AbnList(7),
    SugList(8), Setting(9), WorkNoteList(10), WorkItemList(11), WorkItemStatusList(12);
    private final int value;

    private enumFunc(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static enumFunc fromId(int id) {
        for (enumFunc type : enumFunc.values()) {
            if (type.getValue() == id) {
                return type;
            }
        }
        return null;
    }
}
*/