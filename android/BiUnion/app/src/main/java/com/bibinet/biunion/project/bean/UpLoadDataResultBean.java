package com.bibinet.biunion.project.bean;

import java.io.Serializable;

/**
 * Created by bibinet on 2017-7-3.
 */

public class UpLoadDataResultBean implements Serializable {

    /**
     * resCode : UPenterpriseNULLuscCode
     * resMessage : 网络繁忙,请稍后重试
     * url : http://202.99.212.204:8080/iip/user/enterprise.json
     */

    private String resCode;
    private String resMessage;
    private UserBean user;

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getResMessage() {
        return resMessage;
    }

    public void setResMessage(String resMessage) {
        this.resMessage = resMessage;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public static class UserBean {
        /**
         * image :
         * enterprise : {"businessLicenseCardNo":"","USCCode":"","businessLicenseName":"","contactName":"jdjjcjd","industry":"","contactCellphone":"18295715877","enterpriseId":"100578","region":"","addr":"","enterpriseName":"jfjdj","tradingCertificateURL":""}
         * name : jfjdj
         * userId : 101346
         */

        private String image;
        private EnterpriseBean enterprise;
        private String name;
        private String userId;

        private String emchatUserName;
        private String emchatPassword;

        public String getEmchatUserName() {
            return emchatUserName;
        }

        public void setEmchatUserName(String emchatUserName) {
            this.emchatUserName = emchatUserName;
        }

        public String getEmchatPassword() {
            return emchatPassword;
        }

        public void setEmchatPassword(String emchatPassword) {
            this.emchatPassword = emchatPassword;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public EnterpriseBean getEnterprise() {
            return enterprise;
        }

        public void setEnterprise(EnterpriseBean enterprise) {
            this.enterprise = enterprise;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public static class EnterpriseBean {
            /**
             * businessLicenseCardNo :
             * USCCode :
             * businessLicenseName :
             * contactName : jdjjcjd
             * industry :
             * contactCellphone : 18295715877
             * enterpriseId : 100578
             * region :
             * addr :
             * enterpriseName : jfjdj
             * tradingCertificateURL :
             */
            private String businessLicenseCardNo;
            private String USCCode;
            private String businessLicenseName;
            private String contactName;
            private String industry;
            private String contactCellphone;
            private String enterpriseCode;
            private String region;
            private String addr;
            private String enterpriseName;
            private String tradingCertificateURL;

            public String getBusinessLicenseCardNo() {
                return businessLicenseCardNo;
            }

            public void setBusinessLicenseCardNo(String businessLicenseCardNo) {
                this.businessLicenseCardNo = businessLicenseCardNo;
            }

            public String getUSCCode() {
                return USCCode;
            }

            public void setUSCCode(String USCCode) {
                this.USCCode = USCCode;
            }

            public String getBusinessLicenseName() {
                return businessLicenseName;
            }

            public void setBusinessLicenseName(String businessLicenseName) {
                this.businessLicenseName = businessLicenseName;
            }

            public String getContactName() {
                return contactName;
            }

            public void setContactName(String contactName) {
                this.contactName = contactName;
            }

            public String getIndustry() {
                return industry;
            }

            public void setIndustry(String industry) {
                this.industry = industry;
            }

            public String getContactCellphone() {
                return contactCellphone;
            }

            public void setContactCellphone(String contactCellphone) {
                this.contactCellphone = contactCellphone;
            }

            public String getEnterpriseCode() {
                return enterpriseCode;
            }

            public void setEnterpriseCode(String enterpriseCode) {
                this.enterpriseCode = enterpriseCode;
            }

            public String getRegion() {
                return region;
            }

            public void setRegion(String region) {
                this.region = region;
            }

            public String getAddr() {
                return addr;
            }

            public void setAddr(String addr) {
                this.addr = addr;
            }

            public String getEnterpriseName() {
                return enterpriseName;
            }

            public void setEnterpriseName(String enterpriseName) {
                this.enterpriseName = enterpriseName;
            }

            public String getTradingCertificateURL() {
                return tradingCertificateURL;
            }

            public void setTradingCertificateURL(String tradingCertificateURL) {
                this.tradingCertificateURL = tradingCertificateURL;
            }
        }
    }

}
