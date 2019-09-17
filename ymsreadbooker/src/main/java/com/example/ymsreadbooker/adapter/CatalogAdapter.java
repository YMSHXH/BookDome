package com.example.ymsreadbooker.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.ymsreadbooker.R;
import com.example.ymsreadbooker.bean.InfoVo;

import java.util.List;

/**
 * FileName: CatalogAdapter
 * Author: xh`yang
 * Date: 2019/9/17 15:32
 * Description: 目录
 */
public class CatalogAdapter extends BaseQuickAdapter<InfoVo,BaseViewHolder>{
    public CatalogAdapter(@Nullable List<InfoVo> data) {
        super(R.layout.item_catalog,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, InfoVo item) {
        helper.setText(R.id.cat_title,item.getTitle());
    }
}
