package com.sy.side.stock.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@Document(indexName = "stock_item_master_v1")
public class StockSearchDocument {

    @Id
    private String id;

    @Field(name = "srtn_cd", type = FieldType.Keyword)
    private String srtnCd;

    @Field(name = "isin_cd", type = FieldType.Keyword)
    private String isinCd;

    @Field(name = "mrkt_ctg", type = FieldType.Keyword)
    private String mrktCtg;

    @Field(name = "itms_nm", type = FieldType.Text)
    private String itmsNm;

    @Field(name = "corp_nm", type = FieldType.Text)
    private String corpNm;

    @Field(name = "active_yn", type = FieldType.Keyword)
    private String activeYn;

    @Field(name = "bas_dt", type = FieldType.Keyword)
    private String basDt;
}
