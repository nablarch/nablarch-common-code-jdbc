package nablarch.common.code;

import nablarch.test.support.SystemRepositoryResource;
import nablarch.test.support.db.helper.DatabaseTestRunner;
import nablarch.test.support.db.helper.VariousDbTestHelper;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Locale;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;

@RunWith(DatabaseTestRunner.class)
public class BasicCodeLoaderTest {

    @Rule
    public SystemRepositoryResource repositoryResource = new SystemRepositoryResource("nablarch/common/code/basic-code-loader-test.xml");

    @BeforeClass
    public static void classSetup() throws Exception {

        VariousDbTestHelper.createTable(CodeName.class);
        VariousDbTestHelper.createTable(CodePattern.class);

        VariousDbTestHelper.setUpTable(
                new CodePattern("0001", "01", "1", "0", "0"),
                new CodePattern("0001", "02", "1", "0", "0"),
                new CodePattern("0002", "01", "1", "0", "0"),
                new CodePattern("0002", "02", "1", "0", "0"),
                new CodePattern("0002", "03", "0", "1", "0"),
                new CodePattern("0002", "04", "0", "1", "0"),
                new CodePattern("0002", "05", "1", "0", "0"),
                new CodePattern("9999", "01", "1", "1", "1"),
                new CodePattern("9999", "02", "1", "1", "1")
        );

        VariousDbTestHelper.setUpTable(
                new CodeName("0001", "01", "en", 2L, "Male", "M", "01:Male", "0001-01-en"),
                new CodeName("0001", "02", "en", 1L, "Female", "F", "02:Female", "0001-02-en"),
                new CodeName("0002", "01", "en", 1L, "Initial State", "Initial", "", "0002-01-en"),
                new CodeName("0002", "02", "en", 2L, "Waiting For Batch Start", "Waiting", "", "0002-02-en"),
                new CodeName("0002", "03", "en", 3L, "Batch Running", "Running", "", "0002-03-en"),
                new CodeName("0002", "04", "en", 4L, "Batch Execute Completed Checked", "Completed", "", "0002-04-en"),
                new CodeName("0002", "05", "en", 5L, "Batch Result Checked", "Checked", "", "0002-05-en"),
                new CodeName("0001", "01", "ja", 1L, "男性", "男", "01:Male", "0001-01-ja"),
                new CodeName("0001", "02", "ja", 2L, "女性", "女", "02:Female", "0001-02-ja"),
                new CodeName("0002", "01", "ja", 1L, "初期状態", "初期", "", "0002-01-ja"),
                new CodeName("0002", "02", "ja", 2L, "処理開始待ち", "待ち", "", "0002-02-ja"),
                new CodeName("0002", "03", "ja", 3L, "処理実行中", "実行", "", "0002-03-ja"),
                new CodeName("0002", "04", "ja", 4L, "処理実行完了", "完了", "", "0002-04-ja"),
                new CodeName("0002", "05", "ja", 5L, "処理結果確認完了", "確認", "", "0002-05-ja"),
                new CodeName("9999", "01", "ja", 1L, "[\uD83D\uDE01\uD83D\uDE01\uD83D\uDE01]", "\uD83D\uDE01", "01:[\uD83D\uDE01\uD83D\uDE01\uD83D\uDE01]", ""),
                new CodeName("9999", "02", "ja", 2L, "[\uD83D\uDE0E\uD83D\uDE0E\uD83D\uDE0E]", "\uD83D\uDE0E", "02:[\uD83D\uDE0E\uD83D\uDE0E\uD83D\uDE0E]", "")
        );
    }

    @Test
    public void testGetValue() throws Exception {
        BasicCodeLoader codeLoader = repositoryResource.getComponentByType(BasicCodeLoader.class);
        Code code0001 = codeLoader.getValue("0001");
        Code code0002 = codeLoader.getValue("0002");

        assertNull(codeLoader.getValue("0003"));

        assertEquals("0001", codeLoader.getId(code0001));
        assertEquals("0002", codeLoader.getId(code0002));

        assertEquals("0001", code0001.getCodeId());
        assertEquals("Male", code0001.getName("01", Locale.ENGLISH));
        assertEquals("Female", code0001.getName("02", Locale.ENGLISH));
        assertEquals("M", code0001.getShortName("01", Locale.ENGLISH));
        assertEquals("F", code0001.getShortName("02", Locale.ENGLISH));
        assertEquals("0001-01-en", code0001.getOptionalName("01", "OPTION01", Locale.ENGLISH));
        assertEquals("0001-02-en", code0001.getOptionalName("02", "OPTION01", Locale.ENGLISH));
        //大文字で登録されているオプションカラム名が小文字で呼ばれた場合
        assertEquals("0001-01-en", code0001.getOptionalName("01", "option01", Locale.ENGLISH));
        assertEquals("0001-02-ja", code0001.getOptionalName("02", "option01", Locale.JAPANESE));

        assertEquals("男性", code0001.getName("01", Locale.JAPANESE));
        assertEquals("女性", code0001.getName("02", Locale.JAPANESE));
        assertEquals("男", code0001.getShortName("01", Locale.JAPANESE));
        assertEquals("女", code0001.getShortName("02", Locale.JAPANESE));
        assertEquals("0001-01-ja", code0001.getOptionalName("01", "OPTION01", Locale.JAPANESE));
        assertEquals("0001-02-ja", code0001.getOptionalName("02", "OPTION01", Locale.JAPANESE));


        assertEquals("0002", code0002.getCodeId());
        assertEquals("Initial State", code0002.getName("01", Locale.ENGLISH));
        assertEquals("Waiting For Batch Start", code0002.getName("02", Locale.ENGLISH));
        assertEquals("Batch Running", code0002.getName("03", Locale.ENGLISH));
        assertEquals("Batch Execute Completed Checked", code0002.getName("04", Locale.ENGLISH));
        assertEquals("Batch Result Checked", code0002.getName("05", Locale.ENGLISH));
        assertEquals("Initial", code0002.getShortName("01", Locale.ENGLISH));
        assertEquals("Waiting", code0002.getShortName("02", Locale.ENGLISH));
        assertEquals("Running", code0002.getShortName("03", Locale.ENGLISH));
        assertEquals("Completed", code0002.getShortName("04", Locale.ENGLISH));
        assertEquals("Checked", code0002.getShortName("05", Locale.ENGLISH));


        assertArrayEquals(new String[]{"02", "01"}, code0001.getValues(Locale.ENGLISH).toArray());
        assertArrayEquals(new String[]{"01", "02", "03", "04", "05"}, code0002.getValues(Locale.ENGLISH).toArray());

        assertArrayEquals(new String[]{"02", "01"}, code0001.getValues("PATTERN1", Locale.ENGLISH).toArray());
        assertArrayEquals(new String[]{"01", "02", "05"}, code0002.getValues("PATTERN1", Locale.ENGLISH).toArray());
        assertArrayEquals(new String[]{}, code0001.getValues("PATTERN2", Locale.ENGLISH).toArray());
        assertArrayEquals(new String[]{"03", "04"}, code0002.getValues("PATTERN2", Locale.ENGLISH).toArray());
        assertArrayEquals(new String[]{}, code0001.getValues("PATTERN3", Locale.ENGLISH).toArray());
        assertArrayEquals(new String[]{}, code0002.getValues("PATTERN3", Locale.ENGLISH).toArray());


        assertEquals("初期状態", code0002.getName("01", Locale.JAPANESE));
        assertEquals("処理開始待ち", code0002.getName("02", Locale.JAPANESE));
        assertEquals("処理実行中", code0002.getName("03", Locale.JAPANESE));
        assertEquals("処理実行完了", code0002.getName("04", Locale.JAPANESE));
        assertEquals("処理結果確認完了", code0002.getName("05", Locale.JAPANESE));
        assertEquals("初期", code0002.getShortName("01", Locale.JAPANESE));
        assertEquals("待ち", code0002.getShortName("02", Locale.JAPANESE));
        assertEquals("実行", code0002.getShortName("03", Locale.JAPANESE));
        assertEquals("完了", code0002.getShortName("04", Locale.JAPANESE));
        assertEquals("確認", code0002.getShortName("05", Locale.JAPANESE));

        assertArrayEquals(new String[]{"01", "02"}, code0001.getValues(Locale.JAPANESE).toArray());
        assertArrayEquals(new String[]{"01", "02", "03", "04", "05"}, code0002.getValues(Locale.JAPANESE).toArray());

        assertArrayEquals(new String[]{"01", "02"}, code0001.getValues("PATTERN1", Locale.JAPANESE).toArray());
        assertArrayEquals(new String[]{"01", "02", "05"}, code0002.getValues("PATTERN1", Locale.JAPANESE).toArray());
        assertArrayEquals(new String[]{}, code0001.getValues("PATTERN2", Locale.JAPANESE).toArray());
        assertArrayEquals(new String[]{"03", "04"}, code0002.getValues("PATTERN2", Locale.JAPANESE).toArray());
        assertArrayEquals(new String[]{}, code0001.getValues("PATTERN3", Locale.JAPANESE).toArray());
        assertArrayEquals(new String[]{}, code0002.getValues("PATTERN3", Locale.JAPANESE).toArray());
        //大文字で登録されているオプションカラム名が小文字で呼ばれた場合
        assertArrayEquals(new String[]{}, code0001.getValues("pattern3", Locale.JAPANESE).toArray());
        assertArrayEquals(new String[]{}, code0002.getValues("pattern3", Locale.JAPANESE).toArray());
        assertTrue(code0002.contains("pattern1", "01"));
        assertTrue(code0002.contains("pattern2", "03"));


        // 対応していない言語を指定した場合、例外
        try {
            code0002.getName("01", Locale.CHINESE);
            fail("例外が発生するはず。");
        } catch (IllegalArgumentException e) {
            // OK
        }
        try {
            code0002.getShortName("01", Locale.CHINESE);
            fail("例外が発生するはず。");
        } catch (IllegalArgumentException e) {
            // OK
        }
        try {
            code0002.getOptionalName("01", "OPTION01", Locale.CHINESE);
            fail("例外が発生するはず。");
        } catch (IllegalArgumentException e) {
            // OK
        }
        try {
            code0002.getValues(Locale.CHINESE);
            fail("例外が発生するはず。");
        } catch (IllegalArgumentException e) {
            // OK
        }
        try {
            code0002.getValues("PATTERN1", Locale.CHINESE);
            fail("例外が発生するはず。");
        } catch (IllegalArgumentException e) {
            // OK
        }

        // 存在しない値を指定した場合、例外が発生する
        try {
            code0002.getName("00", Locale.ENGLISH);
            fail("例外が発生するはず。");
        } catch (IllegalArgumentException e) {
            // OK
        }

        // 存在しないオプション名称を指定した場合、例外が発生する
        try {
            code0002.getOptionalName("01", "OPTION02", Locale.ENGLISH);
            fail("例外が発生するはず。");
        } catch (IllegalArgumentException e) {
            // OK
        }

        // 存在しないパターンを指定した場合、例外が発生する
        try {
            code0002.getValues("PATTERN4", Locale.ENGLISH);
            fail("例外が発生するはず。");
        } catch (IllegalArgumentException e) {
            assertEquals("pattern was not found. code id = 0002, locale = en, pattern = PATTERN4", e.getMessage());
        }

    }

    @Test
    public void testLoadAll() throws Exception {
        BasicCodeLoader codeLoader = repositoryResource.getComponentByType(BasicCodeLoader.class);
        List<Code> allCodes = codeLoader.loadAll();

        assertEquals(3, allCodes.size());

        Code code0001 = allCodes.get(0);
        Code code0002 = allCodes.get(1);

        assertEquals("0001", codeLoader.getId(code0001));
        assertEquals("0002", codeLoader.getId(code0002));

        assertEquals("0001", code0001.getCodeId());
        assertEquals("Male", code0001.getName("01", Locale.ENGLISH));
        assertEquals("Female", code0001.getName("02", Locale.ENGLISH));
        assertEquals("M", code0001.getShortName("01", Locale.ENGLISH));
        assertEquals("F", code0001.getShortName("02", Locale.ENGLISH));
        assertEquals("0001-01-en", code0001.getOptionalName("01", "OPTION01", Locale.ENGLISH));
        assertEquals("0001-02-en", code0001.getOptionalName("02", "OPTION01", Locale.ENGLISH));

        assertEquals("男性", code0001.getName("01", Locale.JAPANESE));
        assertEquals("女性", code0001.getName("02", Locale.JAPANESE));
        assertEquals("男", code0001.getShortName("01", Locale.JAPANESE));
        assertEquals("女", code0001.getShortName("02", Locale.JAPANESE));
        assertEquals("0001-01-ja", code0001.getOptionalName("01", "OPTION01", Locale.JAPANESE));
        assertEquals("0001-02-ja", code0001.getOptionalName("02", "OPTION01", Locale.JAPANESE));


        assertEquals("0002", code0002.getCodeId());
        assertEquals("Initial State", code0002.getName("01", Locale.ENGLISH));
        assertEquals("Waiting For Batch Start", code0002.getName("02", Locale.ENGLISH));
        assertEquals("Batch Running", code0002.getName("03", Locale.ENGLISH));
        assertEquals("Batch Execute Completed Checked", code0002.getName("04", Locale.ENGLISH));
        assertEquals("Batch Result Checked", code0002.getName("05", Locale.ENGLISH));
        assertEquals("Initial", code0002.getShortName("01", Locale.ENGLISH));
        assertEquals("Waiting", code0002.getShortName("02", Locale.ENGLISH));
        assertEquals("Running", code0002.getShortName("03", Locale.ENGLISH));
        assertEquals("Completed", code0002.getShortName("04", Locale.ENGLISH));
        assertEquals("Checked", code0002.getShortName("05", Locale.ENGLISH));
        assertEquals("0002-01-en", code0002.getOptionalName("01", "OPTION01", Locale.ENGLISH));
        assertEquals("0002-02-en", code0002.getOptionalName("02", "OPTION01", Locale.ENGLISH));
        assertEquals("0002-03-en", code0002.getOptionalName("03", "OPTION01", Locale.ENGLISH));
        assertEquals("0002-04-en", code0002.getOptionalName("04", "OPTION01", Locale.ENGLISH));
        assertEquals("0002-05-en", code0002.getOptionalName("05", "OPTION01", Locale.ENGLISH));


        assertEquals("初期状態", code0002.getName("01", Locale.JAPANESE));
        assertEquals("処理開始待ち", code0002.getName("02", Locale.JAPANESE));
        assertEquals("処理実行中", code0002.getName("03", Locale.JAPANESE));
        assertEquals("処理実行完了", code0002.getName("04", Locale.JAPANESE));
        assertEquals("処理結果確認完了", code0002.getName("05", Locale.JAPANESE));
        assertEquals("初期", code0002.getShortName("01", Locale.JAPANESE));
        assertEquals("待ち", code0002.getShortName("02", Locale.JAPANESE));
        assertEquals("実行", code0002.getShortName("03", Locale.JAPANESE));
        assertEquals("完了", code0002.getShortName("04", Locale.JAPANESE));
        assertEquals("確認", code0002.getShortName("05", Locale.JAPANESE));
        assertEquals("0002-01-ja", code0002.getOptionalName("01", "OPTION01", Locale.JAPANESE));
        assertEquals("0002-02-ja", code0002.getOptionalName("02", "OPTION01", Locale.JAPANESE));
        assertEquals("0002-03-ja", code0002.getOptionalName("03", "OPTION01", Locale.JAPANESE));
        assertEquals("0002-04-ja", code0002.getOptionalName("04", "OPTION01", Locale.JAPANESE));
        assertEquals("0002-05-ja", code0002.getOptionalName("05", "OPTION01", Locale.JAPANESE));
        //大文字で登録されているオプションカラム名が小文字で呼ばれた場合
        assertEquals("0002-01-ja", code0002.getOptionalName("01", "option01", Locale.JAPANESE));
        assertEquals("0002-01-en", code0002.getOptionalName("01", "option01", Locale.ENGLISH));

    }

    @Test
    public void testLoadAllMultiCall() throws Exception {
        BasicCodeLoader codeLoader = repositoryResource.getComponentByType(BasicCodeLoader.class);

        // loadAllを2回呼び出して、SQL文が2回作られないか確認(コードカバレッジ対応)
        codeLoader.loadAll();
        codeLoader.loadAll();

    }

    @Test
    public void testNotImplementedMethods() {
        BasicCodeLoader codeLoader = repositoryResource.getComponentByType(BasicCodeLoader.class);

        assertNull(codeLoader.generateIndexKey("", null));
        assertNull(codeLoader.getIndexNames());
        assertNull(codeLoader.getValues("", ""));
    }

    @Test
    public void testWithoutPattern() {
        BasicCodeLoader codeLoader = repositoryResource.getComponentByType(BasicCodeLoader.class);

        // 存在しないパターンを指定した場合、例外が発生する。
        try {
            assertFalse(codeLoader.getValue("0002").contains("PATTERN4", "01"));
            fail("例外が発生するはず。");
        } catch (IllegalArgumentException e) {
            // OK
        }

        try {
            codeLoader.getValue("0002").getValues("PATTERN4", Locale.JAPANESE);
            fail("例外が発生するはず。");
        } catch (IllegalArgumentException e) {
            // OK
        }

        // 通常の値はそのまま取得できる。
        assertEquals("初期状態", codeLoader.getValue("0002").getName("01", Locale.JAPANESE));
    }

    @Test
    public void testWithoutOption() {
        BasicCodeLoader codeLoader = repositoryResource.getComponentByType(BasicCodeLoader.class);

        // オプション名称が存在しなかった場合、例外が発生する。
        try {
            codeLoader.getValue("0002").getOptionalName("01", "OPTION02", Locale.JAPANESE);
            fail("例外が発生するはず。");
        } catch (IllegalArgumentException e) {
            // OK
        }

        // 通常の値はそのまま取得できる。
        assertEquals("初期状態", codeLoader.getValue("0002").getName("01", Locale.JAPANESE));
    }

    /**
     * {@link CodeUtil}経由でサロゲートペアが扱えることを確認するケース。
     */
    @Test
    public void testSurrogatePair() {
        assertThat(CodeUtil.getName("9999", "01"), is("[\uD83D\uDE01\uD83D\uDE01\uD83D\uDE01]"));
        assertThat(CodeUtil.getShortName("9999", "02"), is("\uD83D\uDE0E"));
        assertThat(CodeUtil.getOptionalName("9999", "02", "NAME_WITH_VALUE"),
                is("02:[\uD83D\uDE0E\uD83D\uDE0E\uD83D\uDE0E]"));
    }
}
