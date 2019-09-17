package com.example.ymsreadbooker.adapter;

import android.support.annotation.Nullable;
import android.support.design.card.MaterialCardView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.ymsreadbooker.R;

import java.util.List;

/**
 * FileName: ReadBackAdater
 * Author: xh`yang
 * Date: 2019/9/17 13:54
 * Description:
 */
public class ReadBackAdater extends BaseQuickAdapter<Integer,BaseViewHolder> {


    public ReadBackAdater(@Nullable List<Integer> data) {
        super(R.layout.item_begcolor,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Integer item) {
        //cardBackgroundColor
        MaterialCardView materialCardView = (MaterialCardView) helper.getView(R.id.card_view);
        materialCardView.setCardBackgroundColor(item);
    }
}
