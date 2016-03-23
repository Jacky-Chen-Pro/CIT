package com.android.incongress.cd.conference;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.incongress.cd.conference.adapters.SearchResultAdapter;
import com.android.incongress.cd.conference.beans.ClassesBean;
import com.android.incongress.cd.conference.beans.MeetingBean;
import com.android.incongress.cd.conference.beans.SessionBean;
import com.android.incongress.cd.conference.fragments.meeting_schedule.SessionDetailFragment;
import com.mobile.incongress.cd.conference.basic.csccm.R;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Jacky on 2016/3/11.
 * 查询结果
 */
public class SearchResultActivity extends FragmentActivity {
    private ListView mLvSearchResult;
    private SearchResultAdapter mAdapter;
    private List<SessionBean> mSessionBeans;
    private List<MeetingBean> mMeetingBeans;
    private List<ClassesBean> mClasses;
    private String mResultNum = "0";

    private ImageView mIvBack;
    private TextView mTvNoDataTips,mTvTitle;

    private static String BUNDLE_SESSION = "session";
    private static String BUNDLE_MEETING = "meeting";
    private static String BUNDLE_CLASSES = "classes";
    private static String BUNDLE_RESULT_NUM = "resultNum";

    public static final void startSearchResultActivity(Context context, List<SessionBean> sessions,List<MeetingBean> meetings, List<ClassesBean> classes, String resultNum) {
        Intent intent = new Intent();
        intent.setClass(context, SearchResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_SESSION , (Serializable) sessions);
        bundle.putSerializable(BUNDLE_MEETING, (Serializable) meetings);
        bundle.putSerializable(BUNDLE_CLASSES, (Serializable) classes);
        bundle.putString(BUNDLE_RESULT_NUM, resultNum);
        intent.putExtra("bundle", bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search_result);

        Bundle bundle = getIntent().getBundleExtra("bundle");
        mSessionBeans = (List<SessionBean>) bundle.getSerializable(BUNDLE_SESSION);
        mMeetingBeans = (List<MeetingBean>) bundle.getSerializable(BUNDLE_MEETING);
        mClasses = (List<ClassesBean>) bundle.getSerializable(BUNDLE_CLASSES);
        mResultNum = bundle.getString(BUNDLE_RESULT_NUM);

        mIvBack = (ImageView) findViewById(R.id.title_back);
        mTvTitle = (TextView) findViewById(R.id.title_text);
        mTvNoDataTips = (TextView) findViewById(R.id.tv_tips);

        mTvTitle.setText(getString(R.string.search_search_result_title, mResultNum));

        mLvSearchResult = (ListView) findViewById(R.id.lv_search_result);

        if(mSessionBeans.size() == 0) {
            mTvNoDataTips.setVisibility(View.VISIBLE);
            mLvSearchResult.setVisibility(View.GONE);
        }else {
            mAdapter = new SearchResultAdapter(this, mSessionBeans, mMeetingBeans, mClasses);
            mLvSearchResult.setAdapter(mAdapter);

            mLvSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SessionBean session = mSessionBeans.get(position);

//                    SessionDetailFragment detail = new SessionDetailFragment();
//                    detail.setArguments(getSessionPosition(session.getSessionGroupId()), mAllSessionList, mClassBeans);
//                    action(detail, R.string.meeting_schedule_detail_title, false, false, false);
                }
            });
        }

        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchResultActivity.this.finish();
            }
        });
    }
}
