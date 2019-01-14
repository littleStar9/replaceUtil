public enum ScMultiEnum {

    SC_MULTI_MSG_0001("aaaa", "批量盘点");
    String multiCode;
    String content;
    ScMultiEnum(String multiCode, String content){
        this.multiCode = multiCode;
        this.content = content;
    }

    public String getMultiCode() {
        return multiCode;
    }

    public void setMultiCode(String multiCode) {
        this.multiCode = multiCode;
    }

    public String getName() {
        return name();
    }

}
