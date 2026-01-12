package pub.module.excel.biz.util;


import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * Excel写入测试类
 * 用于测试Excel导出功能
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
//@Ignore("手动测试")
public class JExcelWriterTest {

    public static Order testJExcelWriter() {
        Order order = new Order();
        long startMillis = System.currentTimeMillis();

        OrderItem item1 = new OrderItem();
        item1.setIndex(1);
        item1.setIntegral(30);
        item1.setNum(2);
        item1.setProductNm("商品B");
        item1.setSpecNm("红色");
        item1.setStockNo("AFDSE001");
        item1.setUnitPrice(31433.15);
        item1.setTotalPrice(62866.3);
        item1.setRemark("这是第1行");
        order.getOrderItems().add(item1);

        OrderItem item2 = new OrderItem();
        item2.setIndex(2);
        item2.setIntegral(50);
        item2.setNum(3);
        item2.setProductNm("商品W");
        item2.setSpecNm("");
        item2.setStockNo("FDWEE345");
        item2.setUnitPrice(32.15);
        item2.setTotalPrice(96.45);
        item2.setRemark("第二行");
        order.getOrderItems().add(item2);

        //导出EXCEL最大行数（近似），因为还有页头页脚
        for (int i = 0; i < 100; i++) {
            order.getOrderItems().add(item1);
            order.getOrderItems().add(item2);
        }
        return order;

    }

    public static void main(String[] args) {
        String result = JSONUtil.toJsonStr(testJExcelWriter());
        File file = new File("E:\\temp\\json.json");
        FileUtil.writeString(result, file, "UTF-8");
    }

    public static class OrderItem {
        private Integer index;
        private String productNm;
        private String stockNo;
        private String specNm;
        private Double unitPrice;
        private Integer num;
        private Integer integral;
        private Double totalPrice;
        private String remark;

        public String getProductNm() {
            return productNm;
        }

        public void setProductNm(String productNm) {
            this.productNm = productNm;
        }

        public String getStockNo() {
            return stockNo;
        }

        public void setStockNo(String stockNo) {
            this.stockNo = stockNo;
        }

        public String getSpecNm() {
            return specNm;
        }

        public void setSpecNm(String specNm) {
            this.specNm = specNm;
        }

        public Double getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(Double unitPrice) {
            this.unitPrice = unitPrice;
        }

        public Integer getNum() {
            return num;
        }

        public void setNum(Integer num) {
            this.num = num;
        }

        public Integer getIntegral() {
            return integral;
        }

        public void setIntegral(Integer integral) {
            this.integral = integral;
        }

        public Double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(Double totalPrice) {
            this.totalPrice = totalPrice;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static class Order {
        private String orderNum = "DFANE00201103030032";
        private Date createDate = new Date();
        private String userName = "用户x";
        private String loginId = "usera";
        private String userEmail = "admin@iloosen.com";
        private String userMobile = "13800138000";
        private String userTel = "020-342432342";
        private String receiverName = "王五";
        private String receiverMobile = "13800138001";
        private String receiverTel = "010-10000";
        private String receiverAddr = "海南省琼海市博鳌镇远洋大道1号";
        private String receiverZipcode = "573199";
        private Double deliveryWayPrice = 10.0;
        private Double pmtTotalPrice = 5.0;
        private Double paidPrice = 8873.0;
        private Double cardPaid = 15.0;
        private Double orderTotalPrice = 8888.0;
        private String invoiceType = "电子发票";
        private String invoiceNum = "141150526233";
        private String remark = "测试打印模板填充";

        private List<OrderItem> orderItems = new ArrayList<OrderItem>();

        public String getOrderNum() {
            return orderNum;
        }

        public Date getCreateDate() {
            return createDate;
        }

        public String getUserName() {
            return userName;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public String getUserMobile() {
            return userMobile;
        }

        public String getUserTel() {
            return userTel;
        }

        public String getCreateDateStr() {
            return SDF.format(createDate);
        }

        public String getReceiverZipcode() {
            return receiverZipcode;
        }

        public void setReceiverZipcode(String receiverZipcode) {
            this.receiverZipcode = receiverZipcode;
        }

        public String getLoginId() {
            return loginId;
        }

        public List<OrderItem> getOrderItems() {
            return orderItems;
        }

        public void setOrderItems(List<OrderItem> orderItems) {
            this.orderItems = orderItems;
        }

        public void setOrderNum(String orderNum) {
            this.orderNum = orderNum;
        }

        public void setCreateDate(Date createDate) {
            this.createDate = createDate;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public void setUserMobile(String userMobile) {
            this.userMobile = userMobile;
        }

        public void setUserTel(String userTel) {
            this.userTel = userTel;
        }

        public String getReceiverName() {
            return receiverName;
        }

        public void setReceiverName(String receiverName) {
            this.receiverName = receiverName;
        }

        public String getReceiverMobile() {
            return receiverMobile;
        }

        public void setReceiverMobile(String receiverMobile) {
            this.receiverMobile = receiverMobile;
        }

        public String getReceiverTel() {
            return receiverTel;
        }

        public void setReceiverTel(String receiverTel) {
            this.receiverTel = receiverTel;
        }

        public String getReceiverAddr() {
            return receiverAddr;
        }

        public void setReceiverAddr(String receiverAddr) {
            this.receiverAddr = receiverAddr;
        }

        public Double getDeliveryWayPrice() {
            return deliveryWayPrice;
        }

        public void setDeliveryWayPrice(Double deliveryWayPrice) {
            this.deliveryWayPrice = deliveryWayPrice;
        }

        public Double getPmtTotalPrice() {
            return pmtTotalPrice;
        }

        public void setPmtTotalPrice(Double pmtTotalPrice) {
            this.pmtTotalPrice = pmtTotalPrice;
        }

        public Double getPaidPrice() {
            return paidPrice;
        }

        public void setPaidPrice(Double paidPrice) {
            this.paidPrice = paidPrice;
        }

        public Double getCardPaid() {
            return cardPaid;
        }

        public void setCardPaid(Double cardPaid) {
            this.cardPaid = cardPaid;
        }

        public Double getOrderTotalPrice() {
            return orderTotalPrice;
        }

        public void setOrderTotalPrice(Double orderTotalPrice) {
            this.orderTotalPrice = orderTotalPrice;
        }

        public String getInvoiceType() {
            return invoiceType;
        }

        public void setInvoiceType(String invoiceType) {
            this.invoiceType = invoiceType;
        }

        public String getInvoiceNum() {
            return invoiceNum;
        }

        public void setInvoiceNum(String invoiceNum) {
            this.invoiceNum = invoiceNum;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public Integer getSumOfNum() {
            return getOrderItems().stream().mapToInt(OrderItem::getNum).sum();
        }

        public Integer getSumOfIntegral() {
            return getOrderItems().stream().mapToInt(OrderItem::getIntegral).sum();
        }

        public Double getSumOfTotalPrice() {
            return getOrderItems().stream().mapToDouble(OrderItem::getTotalPrice).sum();
        }

        public String getPrintTime() {
            return SDF.format(new Date());
        }
    }


}
