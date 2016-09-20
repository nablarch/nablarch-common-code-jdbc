package nablarch.common.code.schema;

import nablarch.common.schema.TableSchema;

/**
 * コード名称テーブルのスキーマ情報を保持するクラス。
 * 
 * @author Koichi Asano
 */
public final class CodePatternSchema extends TableSchema {

    /** コードIDカラムの名前 */
    private String idColumnName;

    /** コード値カラムの名前 */
    private String valueColumnName;

    /** パターンカラムの名前 */
    private String[] patternColumnNames;

    /**
     * コードIDカラムの名前を取得する。
     * @return コードIDカラムの名前
     */
    public String getIdColumnName() {
        return idColumnName;
    }

    /**
     * コードIDカラムの名前を設定する。
     * 
     * @param idColumnName コードIDカラムの名前
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
     * パターンカラムの名前を取得する。
     * 
     * @return パターンカラムの名前
     */
    public String[] getPatternColumnNames() {
        if (patternColumnNames != null) {
            String[] returnValue = new String[patternColumnNames.length];
            System.arraycopy(patternColumnNames, 0, returnValue, 0, patternColumnNames.length);
            return returnValue;
        } else {
            return new String[0];
        }
    }

    /**
     * パターンカラムの名前を設定する。
     * 
     * @param patternColumnNames パターンカラムの名前
     */
    public void setPatternColumnNames(String[] patternColumnNames) {
        String[] newValue = new String[patternColumnNames.length];
        System.arraycopy(patternColumnNames, 0, newValue, 0, patternColumnNames.length);
        this.patternColumnNames = newValue;
    }

    
}
