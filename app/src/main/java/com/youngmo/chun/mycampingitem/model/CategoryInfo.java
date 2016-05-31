package com.youngmo.chun.mycampingitem.model;

/**
 * Created by ChunYoungmo on 15. 11. 4..
 */
public class CategoryInfo {
    /**< 카테고리 ID (카테고리 정보 DB 테이블에서의 PK로 사용됨 - insert 시에 autoincrement로 자동 증가) */
    private int     mId;
    /**< 카테고리 명 */
    private String  mName;

    public CategoryInfo(int id, String name) {
        this.mId = id;
        this.mName = name;
    }

    public int getId() {
        return this.mId;
    }

    public String getName() {
        return this.mName;
    }
}
