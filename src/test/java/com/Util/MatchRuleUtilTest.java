package com.Util;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MatchRuleUtilTest {
  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void isCorrectCode() {
    Assert.assertTrue(MatchRuleUtil.isCorrectString(MatchRuleUtil.isContCode,"535YOPDOP2018030002002"));
    Assert.assertTrue(MatchRuleUtil.isCorrectString(MatchRuleUtil.isContCode,"20201807100008"));
    Assert.assertTrue(MatchRuleUtil.isCorrectString(MatchRuleUtil.isContCode,"316Y/OPD.OP.201811.0008"));
    Assert.assertFalse(MatchRuleUtil.isCorrectString(MatchRuleUtil.isContCode,"%22+oNmOuSeOvEr%3Datpajydc"));

  }
  @Test
  public void isCorrectBody() {
    Assert.assertTrue(MatchRuleUtil.isCorrectString(MatchRuleUtil.isContBody,"6529419395221"));
    Assert.assertFalse(MatchRuleUtil.isCorrectString(MatchRuleUtil.isContBody,"%22+oNmOuSeOvEr%3Datpajydc"));
    Assert.assertFalse(MatchRuleUtil.isCorrectString(MatchRuleUtil.isContBody,"aunomzup"));

  }

  @Test
  public void isCorrectTableName() {
    String[] tableNames={"zthr_etl_log","w_sftcyz_sftcyzsaascptce39600b_1","w_ics_bbcs_5"};
    String  matchString = MatchRuleUtil.isContType;
    for (String tableName : tableNames) {
      Assert.assertTrue(MatchRuleUtil.isCorrectString(matchString,tableName));
    }
    Assert.assertFalse(MatchRuleUtil.isCorrectString(matchString,"%22+oNmOuSeOvEr%3Datpajydc"));
  }

  @Test
  public void isCorrectName() {
    String[] Names={"法人账户透资合同关联公司清单（乙方）","2北京区陶景素便利店合同-正文"};
    String  matchString = MatchRuleUtil.isName;
    for (String Name : Names) {
      Assert.assertTrue(MatchRuleUtil.isCorrectString(matchString,Name));
    }
    Assert.assertFalse(MatchRuleUtil.isCorrectString(matchString,"%22+oNmOuSeOvEr%3Datpajydc"));
  }

  @Test
  public void isCorrectBusiKey() {
    String[] tableNames={"ZX202002120506","SF_PSMP_ZLFBHT-123131313","1"};
    String  matchString = MatchRuleUtil.isBusiKey;
    for (String tableName : tableNames) {
      Assert.assertTrue(MatchRuleUtil.isCorrectString(matchString,tableName));
    }
    Assert.assertFalse(MatchRuleUtil.isCorrectString(matchString,"%22+oNmOuSeOvEr%3Datpajydc"));
  }
  @Test
  public void isCorrectChangeType() {
    String[] tableNames={"运价变更"};
    String  matchString = MatchRuleUtil.isChangeType;
    for (String tableName : tableNames) {
      Assert.assertTrue(MatchRuleUtil.isCorrectString(matchString,tableName));
    }
    Assert.assertFalse(MatchRuleUtil.isCorrectString(matchString,"%22+oNmOuSeOvEr%3Datpajydc"));
  }

  @Test
  public void isCorrectLinuxTime() {
    String[] tableNames={"1571214821000"};
    String  matchString = MatchRuleUtil.isLinuxTime;
    for (String tableName : tableNames) {
      Assert.assertTrue(MatchRuleUtil.isCorrectString(matchString,tableName));
    }
    Assert.assertFalse(MatchRuleUtil.isCorrectString(matchString,"%22+oNmOuSeOvEr%3Datpajydc"));
  }
  @Test
  public void isCorrectDateString() {
    String[] tableNames={"9999-12-31"};
    String  matchString = MatchRuleUtil.isDateString;
    for (String tableName : tableNames) {
      Assert.assertTrue(MatchRuleUtil.isCorrectString(matchString,tableName));
    }
    Assert.assertFalse(MatchRuleUtil.isCorrectString(matchString,"%22+oNmOuSeOvEr%3Datpajydc"));
  }
  @Test
  public void isCorrectFilePath() {
    String[] tableNames={"/nfsc/ESG_ICS_CORE/public/contractText/files/27/702e2ba2dd05b6206e074e72e2fa567e.docx",
        "/public/contractText/files/27/702e2ba2dd05b6206e074e72e2fa567e.docx","/nfsc/ESG_ICS_CORE",".docx"};
    String  matchString = MatchRuleUtil.isFilePath;
    for (String tableName : tableNames) {
      Assert.assertTrue(MatchRuleUtil.isCorrectString(matchString,tableName));
    }
    Assert.assertFalse(MatchRuleUtil.isCorrectString(matchString,"%22+oNmOuSeOvEr%3Datpajydc"));
  }
  @Test
  public void isCorrectOriginSystem() {
    String[] tableNames={"F2,ab,999999,99,94,93,92,91,90,89,88,87,86,85,84,83,82,81,74,71,59,58,50,47,44,41,32,29,23,20,19,18,17,13,11,10",
        "999999","44"};
    String  matchString = MatchRuleUtil.isOriginSystem;
    for (String tableName : tableNames) {
      Assert.assertTrue(MatchRuleUtil.isCorrectString(matchString,tableName));
    }
    Assert.assertFalse(MatchRuleUtil.isCorrectString(matchString,"%22+oNmOuSeOvEr%3Datpajydc"));
  }
  @Test
  public void isCorrectStateId() {
    String[] tableNames={"15","40"};
    String  matchString = MatchRuleUtil.isStateId;
    for (String tableName : tableNames) {
      Assert.assertTrue(MatchRuleUtil.isCorrectString(matchString,tableName));
    }
    Assert.assertFalse(MatchRuleUtil.isCorrectString(matchString,"%22+oNmOuSeOvEr%3Datpajydc"));
  }




}