package com.android.incongress.cd.conference.fragments.search_schedule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.android.incongress.cd.conference.adapters.SegmentSearchAdapter;
import com.android.incongress.cd.conference.base.BaseFragment;
import com.android.incongress.cd.conference.uis.SegmentedGroup;
import com.android.incongress.cd.conference.utils.ToastUtils;
import com.mobile.incongress.cd.conference.basic.csccm.R;


/**
 * Created by Jacky on 2016/3/7.
 * 日程检索+专家检索
 */
public class NewSearchScheduleFragment extends BaseFragment {
    private ViewPager mViewPager;
    private SegmentSearchAdapter mAdapter;
    private SegmentedGroup mSgTab;
    private RadioButton mRbSchedule,mRbProfessor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_search_fragment, null);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_search);

        mAdapter = new SegmentSearchAdapter(getChildFragmentManager());
        mSgTab = (SegmentedGroup) getActivity().findViewById(R.id.sg_tab);
        mRbSchedule = (RadioButton) getActivity().findViewById(R.id.rb_schedule);
        mRbProfessor = (RadioButton) getActivity().findViewById(R.id.rb_professor);
        mRbSchedule.setChecked(true);
        mSgTab.setVisibility(View.VISIBLE);

        mRbSchedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ToastUtils.showShorToast("日程检索 被选中了");
                    mViewPager.setCurrentItem(0);
                }
            }
        });

        mRbProfessor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ToastUtils.showShorToast("专家检索 被选中了");
                    mViewPager.setCurrentItem(1);
                }
            }
        });

        mViewPager.setAdapter(mAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mRbSchedule.setChecked(true);
                } else {
                    mRbProfessor.setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSgTab.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.sg_tab).setVisibility(View.VISIBLE);
    }
}
