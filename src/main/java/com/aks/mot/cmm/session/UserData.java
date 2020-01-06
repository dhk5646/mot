package com.aks.mot.cmm.session;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserData implements Serializable {

    private static final long serialVersionUID = 4846185108317290650L;

    public static final String USER_DATA_KEY = "VMS_DATA_KEY";

    private String userCd; // 사용자 코드

    private String userId; // 사용자 아이디

    private String comCd; // 회사코드

    private String comGrpCd; // 회사그룹코드

    private String authGrpId; // 권한그룹ID

    private String mapKey; // 맵키

    private String userSprCd; // 사용자 구분코드

    private String langCd; // 언어코드

    private String ymdForm = ""; // 날짜포맷

    private String timeZone = ""; // 타임존

    private String emailAddr = ""; // 이메일

    private String mbpNo = ""; // 휴대폰번호

    private String telNo = ""; // 전화번호

    private String faxNo = ""; // 팩스번호

    private String natnCd = ""; //국가코드

    private String svrIpAddr = "0.0.0.0";

    private String clntIpAddr = "0.0.0.0";

    private String wasInstNm = "";

    private String sid = "";
}
