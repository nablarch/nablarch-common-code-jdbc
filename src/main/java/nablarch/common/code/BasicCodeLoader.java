package nablarch.common.code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nablarch.common.code.schema.CodeNameSchema;
import nablarch.common.code.schema.CodePatternSchema;
import nablarch.core.cache.StaticDataLoader;
import nablarch.core.db.connection.AppDbConnection;
import nablarch.core.db.statement.SqlPStatement;
import nablarch.core.db.statement.SqlResultSet;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.db.transaction.SimpleDbTransactionExecutor;
import nablarch.core.db.transaction.SimpleDbTransactionManager;
import nablarch.core.repository.initialization.Initializable;
import nablarch.core.util.I18NUtil;
import nablarch.core.util.map.CaseInsensitiveMap;

/**
 * データベースからコードをロードするクラス。
 * 
 * @author Koichi Asano
 */
public class BasicCodeLoader implements StaticDataLoader<Code>, Initializable {

    /** 使用するテーブル名／カラム名 */
    private Map<String, String> dbSchema = new HashMap<String, String>();

    /** パターンカラム名 */
    private String[] patternColumnNames;

    /** オプション名称のカラム名 */
    private String[] optionNameColumnNames;

    /** データベーストランザクションマネージャ */
    private SimpleDbTransactionManager dbManager;

    /**
     * 全てのコードをロードするSQL文。
     */
    private String selectAllStatement;

    /**
     * 1つのコードをロードするSQL文。
     */
    private String selectOneCodeStatement;

    /**
     * コード名称テーブルのスキーマ情報を設定する。
     * 
     * @param codeNameSchema コード名称テーブルのスキーマ情報
     */
    public void setCodeNameSchema(CodeNameSchema codeNameSchema) {
        dbSchema.put("codeName", codeNameSchema.getTableName());
        dbSchema.put("codeNameId", codeNameSchema.getIdColumnName());
        dbSchema.put("codeNameValue", codeNameSchema.getValueColumnName());
        dbSchema.put("codeNameLang", codeNameSchema.getLangColumnName());
        dbSchema.put("codeNameSortOrder", codeNameSchema.getSortOrderColumnName());
        dbSchema.put("codeNameName", codeNameSchema.getNameColumnName());
        dbSchema.put("codeNameShortName", codeNameSchema.getShortNameColumnName());
        optionNameColumnNames = codeNameSchema.getOptionNameColumnNames();
    }

    /**
     * コードパターンテーブルのスキーマ情報を設定する。
     * 
     * @param codePatternSchema コードパターンテーブルのスキーマ情報
     */
    public void setCodePatternSchema(CodePatternSchema codePatternSchema) {
        dbSchema.put("codePattern", codePatternSchema.getTableName());
        dbSchema.put("codePatternId", codePatternSchema.getIdColumnName());
        dbSchema.put("codePatternValue", codePatternSchema.getValueColumnName());
        patternColumnNames = codePatternSchema.getPatternColumnNames();
    }

    /**
     * データベーストランザクションマネージャを設定する。
     *
     * @param dbManager データベーストランザクションマネージャ
     */
    public void setDbManager(SimpleDbTransactionManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * {@inheritDoc}<br/>
     * <br/>
     * 
     * 本機能ではインデックスは提供しないためnullを返す。
     */
    public Object generateIndexKey(String indexName, Code value) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Object getId(Code value) {
        return value.getCodeId();
    }

    /**
     * {@inheritDoc}<br/>
     * <br/>
     * 
     * 本機能ではインデックスは提供しないためnullを返す。
     */
    public List<String> getIndexNames() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Code getValue(final Object id) {

        SqlResultSet resultSet = new SimpleDbTransactionExecutor<SqlResultSet>(
                dbManager) {
            @Override
            public SqlResultSet execute(AppDbConnection connection) {
                SqlPStatement statement = connection.prepareStatement(
                        selectOneCodeStatement);
                statement.setString(1, id.toString());
                return statement.retrieve();
            }
        }.doTransaction();

        List<Code> createdCodes = createResult(resultSet);
        if (createdCodes.size() == 1) {
            return createdCodes.get(0);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}<br/>
     * <br/>
     * 
     * 本機能ではインデックスは提供しないためnullを返す。
     */
    public List<Code> getValues(String indexName, Object key) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Code> loadAll() {
        SqlResultSet resultSet = new SimpleDbTransactionExecutor<SqlResultSet>(
                dbManager) {
            @Override
            public SqlResultSet execute(AppDbConnection connection) {
                SqlPStatement statement = connection.prepareStatement(
                        selectAllStatement);
                SqlResultSet results = statement.retrieve();
                return results;
            }
        }.doTransaction();
        return createResult(resultSet);
    }

    /**
     * データベースの検索結果からBasicCodeのListを作成する。
     * 
     * @param queryResults データベースの検索結果
     * @return BasicCodeのList
     */
    private List<Code> createResult(SqlResultSet queryResults) {
        List<Code> basicCodeList = new ArrayList<Code>();

        String codeId = "";
        List<SqlRow> data = new ArrayList<SqlRow>();
        
        for (SqlRow row : queryResults) {
            String currentCodeId = row.getString(dbSchema.get("codeNameId"));
            if (!codeId.equals(currentCodeId)) {
                if (data.size() > 0) {
                    basicCodeList.add(new BasicCode(codeId, data));
                    data.clear();
                }
                codeId = currentCodeId;
            }
            data.add(row);
        }

        if (data.size() > 0) {
            basicCodeList.add(new BasicCode(codeId, data));
        }

        return Collections.unmodifiableList(basicCodeList);
    }

    /**
     * SQL文を初期化する。
     */
    private void initializeStatements() {

        StringBuilder codePatternColumns = new StringBuilder();
        for (String patternColumnName : patternColumnNames) {
            codePatternColumns.append("$codePattern$.");
            codePatternColumns.append(patternColumnName);
            codePatternColumns.append(", ");
        }

        StringBuilder optionNameColumns = new StringBuilder();
        for (String optionNameColumn : optionNameColumnNames) {
            codePatternColumns.append("$codeName$.");
            codePatternColumns.append(optionNameColumn);
            codePatternColumns.append(", ");
        }
        
        String preSelectStatement =
           "SELECT "
                + codePatternColumns
                + optionNameColumns
                + "$codeName$.$codeNameId$, "
                + "$codeName$.$codeNameValue$, "
                + "$codeName$.$codeNameLang$, "
                + "$codeName$.$codeNameName$, "
                + "$codeName$.$codeNameShortName$ "
          + "FROM $codePattern$ "
              + "INNER JOIN $codeName$ "
                  + "ON $codePattern$.$codePatternId$ = $codeName$.$codeNameId$ "
                  + "AND $codePattern$.$codePatternValue$ = $codeName$.$codeNameValue$ ";

        String preWhereStatement =
            "WHERE "
                + "$codePattern$.$codePatternId$ = ? ";

        String preOrderByStatement =
            "ORDER BY "
              + "$codeName$.$codeNameId$, "
              + "$codeName$.$codeNameLang$, "
              + "$codeName$.$codeNameSortOrder$ ";

        String selectStatement = replaceStatement(preSelectStatement);
        String whereStatement = replaceStatement(preWhereStatement);
        String orderByStatement = replaceStatement(preOrderByStatement);

        selectAllStatement = selectStatement + orderByStatement;
        selectOneCodeStatement = selectStatement + whereStatement + orderByStatement;
    }

    /**
     * {@inheritDoc}
     */
    public void initialize() {
        initializeStatements();
    }

    /**
     * SQL文の置き換え文字の置き換えを行う。
     * 
     * @param statement 置き換え対象の文字
     * @return 置き換え文字を置き換えた文字列。
     */
    private String replaceStatement(String statement) {
        Matcher m = Pattern.compile("\\$([_0-9a-zA-Z]+)\\$").matcher(statement);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String replacement = m.group(1);
            m.appendReplacement(sb, dbSchema.get(replacement));
        }
        m.appendTail(sb);
        String createdSelectStatement = sb.toString();
        return createdSelectStatement;
    }

    /**
     * ロードするコードの実装。
     * 
     */
    private final class BasicCode implements Code {

        /**
         * コンストラクタ。
         * 
         * 
         * @param codeId コードID
         * @param data コードを構成するデータのList。<br/>
         *              データは言語を第1の条件としてソートされている必要がある。
         */
        private BasicCode(String codeId, List<SqlRow> data) {
            
            this.codeId = codeId;

            parLangValuesMap = new HashMap<Locale, PerLangValues>();
     
            List<SqlRow> langData = new ArrayList<SqlRow>();
            String lang = "";

            for (SqlRow row : data) {
                String currentLang = row.getString(dbSchema.get("codeNameLang"));
                if (!lang.equals(currentLang)) {
                    if (langData.size() > 0) {
                        PerLangValues value = new PerLangValues(langData);
                        parLangValuesMap.put(I18NUtil.createLocale(lang), value);
                        langData.clear();
                    }

                    lang = currentLang;
                }
                langData.add(row);
            }

            PerLangValues value = new PerLangValues(langData);
            parLangValuesMap.put(I18NUtil.createLocale(lang), value);

            // containsの情報は、全言語文取得
            values = new HashSet<String>();
            patternValuesMap = new CaseInsensitiveMap<Set<String>>();
            for (PerLangValues parLangValues : parLangValuesMap.values()) {
                values.addAll(parLangValues.values);

                for (String pattern : patternColumnNames) {
                    pattern = pattern.toLowerCase();
                    if (!patternValuesMap.containsKey(pattern)) {
                        patternValuesMap.put(pattern, new HashSet<String>());
                    }
                    patternValuesMap.get(pattern).addAll(parLangValues.patternMap.get(pattern));
                }
            }


        }

        /** コードID */
        private final String codeId;

        /** 言語と言語毎に持つ値のMap */
        private final Map<Locale, PerLangValues> parLangValuesMap;

        /** コードに含まれるコード値のセット */
        private final Set<String> values;

        /** パターンに含まれる値のセット */
        private final Map<String, Set<String>> patternValuesMap;

        /**
         * {@inheritDoc}
         */
        public String getCodeId() {
            return codeId;
        }

        /**
         * {@inheritDoc}
         */
        public boolean contains(String value) {
            return values.contains(value);
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean contains(String pattern, String value) {
            Set<String> patternValues = patternValuesMap.get(pattern);
            if (patternValues == null) {
                throw new IllegalArgumentException("pattern was not found. "
                        + "code id = " + codeId
                        + ", pattern = " + pattern);
            }
            return patternValues.contains(value);
        }

        /**
         * {@inheritDoc}
         */
        public String getName(String value, Locale locale) {
            PerLangValues perLangValues = parLangValuesMap.get(locale);
            if (perLangValues == null) {
                throw new IllegalArgumentException("locale was not found. "
                        + "code id = " + codeId
                        + ", locale = " + locale);
            }
            String name = perLangValues.names.get(value);
            if (name == null) {
                throw new IllegalArgumentException("name was not found. "
                        + "code id = " + codeId
                        + ", locale = " + locale
                        + ", value = " + value);
            }
            return name;
        }

        /**
         * {@inheritDoc}
         */
        public String getShortName(String value, Locale locale) {
            PerLangValues parLangValues = parLangValuesMap.get(locale);
            if (parLangValues == null) {
                throw new IllegalArgumentException("locale was not found. "
                        + "code id = " + codeId
                        + ", locale = " + locale);
            }
            String shortName = parLangValues.shortNames.get(value);
            if (shortName == null) {
                throw new IllegalArgumentException("short name was not found. "
                        + "code id = " + codeId
                        + ", locale = " + locale
                        + ", value = " + value);
            }
            return shortName;
        }

        /**
         * {@inheritDoc}
         */
        public String getOptionalName(String value, String optionColumnName,
                Locale locale) {
            PerLangValues parLangValues = parLangValuesMap.get(locale);
            if (parLangValues == null) {
                throw new IllegalArgumentException("locale was not found. "
                        + "code id = " + codeId
                        + ", locale = " + locale);
            }
            Map<String, String> valueMap = parLangValues.optionNamesMap.get(optionColumnName);

            if (valueMap == null) {
                throw new IllegalArgumentException("option name was not found. "
                        + "code id = " + codeId
                        + ", locale = " + locale
                        + ", value = " + value);
            }
            String optionName = valueMap.get(value);
            if (optionName == null) {
                throw new IllegalArgumentException("option name was not found. "
                        + "code id = " + codeId
                        + ", locale = " + locale
                        + ", value = " + value
                        + ", option name = " + optionColumnName);
            }
            return optionName;
        }

        /**
         * {@inheritDoc}
         */
        public List<String> getValues(Locale locale) {
            PerLangValues parLangValues = parLangValuesMap.get(locale);
            if (parLangValues == null) {
                throw new IllegalArgumentException("locale was not found. "
                        + "code id = " + codeId
                        + ", locale = " + locale);
            }
            return parLangValues.values;
        }

        /**
         * {@inheritDoc}
         */
        public List<String> getValues(String pattern, Locale locale) {
            PerLangValues parLangValues = parLangValuesMap.get(locale);
            if (parLangValues == null) {
                throw new IllegalArgumentException("locale was not found. "
                        + "code id = " + codeId
                        + ", locale = " + locale);
            }
            List<String> values = parLangValues.patternMap.get(pattern);
            if (values == null) {
                throw new IllegalArgumentException("pattern was not found. "
                        + "code id = " + codeId
                        + ", locale = " + locale
                        + ", pattern = " + pattern);
            }
            return values;
        }
    }

    /**
     * 言語毎に持つ値を保持するクラス。
     */
    private final class PerLangValues {

        /**
         * 全てのコード値のList。
         */
        private final List<String> values;

        /**
         * パターン毎のコード値のListを保持するMap。
         */
        private final Map<String, List<String>> patternMap;

        /**
         * 名称を保持するMap。<br/>
         * <br/>
         * key:コード値<br/>
         * value:コード名称<br/>
         * 
         */
        private final Map<String, String> names;
    
        /**
         * 略称を保持するMap。<br/>
         * <br/>
         * key:コード値<br/>
         * value:コードの略称<br/>
         * 
         */
        private final Map<String, String> shortNames;

        /**
         * コードのオプション名称を保持するMap。<br/>
         * <br/>
         * key:コード値<br/>
         * value:コードのオプション名称<br/>
         * 
         */
        private final Map<String, Map<String, String>> optionNamesMap;


        /**
         * コンストラクタ。
         * 
         * @param data コードを構成するデータのList。<br/>
         *              データはソート順を第1の条件としてソートされている必要がある。
         */
        private PerLangValues(List<SqlRow> data) {

            List<String> tmpValues = new ArrayList<String>();

            Map<String, String> tmpNames = new HashMap<String, String>();
            Map<String, String> tmpShortNames = new HashMap<String, String>();

            Map<String, Map<String, String>> tmpOptionNamesMap = new CaseInsensitiveMap<Map<String, String>>();
            for (String optionName : optionNameColumnNames) {
                tmpOptionNamesMap.put(optionName.toLowerCase(), new HashMap<String, String>());
            }

            Map<String, List<String>> tmpPatternMap = new CaseInsensitiveMap<List<String>>();
            for (String patternName : patternColumnNames) {
                tmpPatternMap.put(patternName.toLowerCase(), new ArrayList<String>());
            }

            for (SqlRow row : data) {
                String value = row.getString(dbSchema.get("codeNameValue"));
                tmpValues.add(value);
                tmpNames.put(value, row.getString(dbSchema.get("codeNameName")));
                tmpShortNames.put(value, row.getString(dbSchema.get("codeNameShortName")));

                for (String optionName : optionNameColumnNames) {
                    tmpOptionNamesMap.get(optionName).put(value, row.getString(optionName));
                }

                for (String patternName : patternColumnNames) {

                    String patternIsValid = row.getString(patternName);
                    if ("1".equals(patternIsValid)) {
                        tmpPatternMap.get(patternName).add(value);
                    }
                }

            }

            for (Map.Entry<String, Map<String, String>> entry : tmpOptionNamesMap.entrySet()) {
                entry.setValue(Collections.unmodifiableMap(entry.getValue()));
            }

            for (Map.Entry<String, List<String>> entry : tmpPatternMap.entrySet()) {
                entry.setValue(Collections.unmodifiableList(entry.getValue()));
            }

            this.values = Collections.unmodifiableList(tmpValues);
            this.names = Collections.unmodifiableMap(tmpNames);
            this.shortNames = Collections.unmodifiableMap(tmpShortNames);
            this.optionNamesMap = Collections.unmodifiableMap(tmpOptionNamesMap);
            this.patternMap = tmpPatternMap;
        }
    }
}
