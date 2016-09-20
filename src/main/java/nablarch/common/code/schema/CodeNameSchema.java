package nablarch.common.code.schema;

import nablarch.common.schema.TableSchema;

/**
 * コード名称テーブルのスキーマ情報を保持するクラス。
 * 
 * @author Koichi Asano
 */
public final class CodeNameSchema extends TableSchema {

    /** IDカラムの名前 */
    private String idColumnName;

    /** コード値カラムの名前 */
    private String valueColumnName;

    /** 言語カラムの名前 */
    private String langColumnName;

    /** ソート順カラムの名前 */
    private String sortOrderColumnName;

    /** 名称カラムの名前 */
    private String nameColumnName;

    /** 略称カラムの名前 */
    private String shortNameColumnName;

    /** オプション名称カラムの名前 */
    private String[] optionNameColumnNames;

    /**
     * IDカラムの名前を取得する。
     * 
     * @return IDカラムの名前
     */
    public String getIdColumnName() {
        return idColumnName;
    }

    /**
     * IDカラムの名前を設定する。
     * 
     * @param idColumnName idColumnName 
     */
    public void setIdColumnName(String idColumnName) {
        this.idColumnName = idColumnName;
    }

    /**
     * コード値カラムの名前を取得する。
     * 
     * @return コード値カラムの名前
     */
    public String getValueColumnName() {
        return valueColumnName;
    }

    /**
     * コード値カラムの名前を設定する。
     * 
     * @param valueColumnName コード値カラムの名前 
     */
    public void setValueColumnName(String valueColumnName) {
        this.valueColumnName = valueColumnName;
    }

    /**
     * 言語カラムの名前を取得する。
     * 
     * @return 言語カラムの名前
     */
    public String getLangColumnName() {
        return langColumnName;
    }

    /**
     * 言語カラムの名前を設定する。
     * 
     * @param langColumnName 言語カラムの名前 
     */
    public void setLangColumnName(String langColumnName) {
        this.langColumnName = langColumnName;
    }

    /**
     * ソート順カラムの名前 を取得する。
     * 
     * @return ソート順カラムの名前
     */
    public String getSortOrderColumnName() {
        return sortOrderColumnName;
    }

    /**
     * ソート順カラムの名前を設定する。
     * 
     * @param sortOrderColumnName ソート順カラムの名前 
     */
    public void setSortOrderColumnName(String sortOrderColumnName) {
        this.sortOrderColumnName = sortOrderColumnName;
    }

    /**
     * 名称カラムの名前を取得する。
     * 
     * @return 名称カラムの名前
     */
    public String getNameColumnName() {
        return nameColumnName;
    }

    /**
     * 名称カラムの名前を設定する。
     * 
     * @param nameColumnName 名称カラムの名前 
     */
    public void setNameColumnName(String nameColumnName) {
        this.nameColumnName = nameColumnName;
    }

    /**
     * 略称カラムの名前を取得する。
     * 
     * @return 略称カラムの名前
     */
    public String getShortNameColumnName() {
        return shortNameColumnName;
    }

    /**
     * 略称カラムの名前を設定する。
     * 
     * @param shortNameColumnName 略称カラムの名前 
     */
    public void setShortNameColumnName(String shortNameColumnName) {
        this.shortNameColumnName = shortNameColumnName;
    }

    /**
     * オプション名称カラムの名前を取得する。
     * 
     * @return オプション名称カラムの名前
     */
    public String[] getOptionNameColumnNames() {
        if (optionNameColumnNames != null) {
            String[] returnValue = new String[optionNameColumnNames.length];
            System.arraycopy(optionNameColumnNames, 0, returnValue, 0, optionNameColumnNames.length);
            return returnValue;
        } else {
            return new String[0];
        }
    }

    /**
     * オプション名称カラムの名前を設定する。
     * 
     * @param optionNameColumnNames オプション名称カラムの名前 
     */
    public void setOptionNameColumnNames(String[] optionNameColumnNames) {
        String[] newValue = new String[optionNameColumnNames.length];
        System.arraycopy(optionNameColumnNames, 0, newValue, 0, optionNameColumnNames.length);
        this.optionNameColumnNames = newValue;
    }
}
