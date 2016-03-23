package com.android.incongress.cd.conference.fragments.search_schedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.incongress.cd.conference.SearchResultActivity;
import com.android.incongress.cd.conference.adapters.SearchRoomAdapter;
import com.android.incongress.cd.conference.adapters.SearchTimeAdapter;
import com.android.incongress.cd.conference.beans.ClassesBean;
import com.android.incongress.cd.conference.beans.MeetingBean;
import com.android.incongress.cd.conference.beans.SearchRoomBean;
import com.android.incongress.cd.conference.beans.SearchTimeBean;
import com.android.incongress.cd.conference.beans.SessionBean;
import com.android.incongress.cd.conference.data.ConferenceGetData;
import com.android.incongress.cd.conference.data.ConferenceTableField;
import com.android.incongress.cd.conference.data.ConferenceTables;
import com.android.incongress.cd.conference.data.DbAdapter;
import com.android.incongress.cd.conference.uis.LWheelDialog;
import com.android.incongress.cd.conference.uis.SpaceItemDecoration;
import com.android.incongress.cd.conference.utils.DensityUtil;
import com.android.incongress.cd.conference.utils.DialogUtil;
import com.android.incongress.cd.conference.utils.StringUtils;
import com.android.incongress.cd.conference.utils.ToastUtils;
import com.mobile.incongress.cd.conference.basic.csccm.R;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.util.JLogger;

/**
 * Created by Jacky on 2016/3/8.
 * 讲者检索中的讲者页面
 */
public class SegmentScheduleFragment extends Fragment {
    private RecyclerView mRvTime, mRvRoom;
    private LinearLayoutManager mLayoutManager;
    private GridLayoutManager mGridLayoutManager;
    private SearchTimeAdapter mTimeAdapter;
    private List<ClassesBean> mRoomsList;
    private SearchRoomAdapter mRoomAdapter;
    private List<String> mSessionDaysList;
    private TextView mTvCurrentTime;

    private ImageView mIvReset, mIvPrev, mIvLast;
    private int mCurrentTimePosition = 0;
    private DialogUtil dialogUtil;

    //查询条件 日期
    private String mCurrentSearchDay = "";
    //查询条件 会议室ID
    private String mCurrentSearchRoom = "";
    //查询条件 开始时间
    private String mCurrentSearchStartTime = "";
    //查询条件 结束时间
    private String mCurrentSearchEndTime = "";

    private TextView mTvStartSearch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_segment_schedule, null);

        mRvTime = (RecyclerView) view.findViewById(R.id.rv_time);
        mIvReset = (ImageView) view.findViewById(R.id.iv_reset);
        mIvPrev = (ImageView) view.findViewById(R.id.iv_prev);
        mIvLast = (ImageView) view.findViewById(R.id.iv_last);
        mTvCurrentTime = (TextView) view.findViewById(R.id.tv_current_time);
        mTvStartSearch = (TextView) view.findViewById(R.id.tv_start_search);

        dialogUtil = new DialogUtil();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mTimeAdapter = new SearchTimeAdapter(getActivity(), DensityUtil.getScreenSize(getActivity()));
        mRvTime.setAdapter(mTimeAdapter);
        mRvTime.setLayoutManager(mLayoutManager);
        mTimeAdapter.setOnItemClickListener(new SearchTimeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, SearchTimeBean data) {
                int position = data.getPosition();

                if(position == 0) {
                    mCurrentSearchStartTime = "08:00";
                    mCurrentSearchEndTime = "12:00";
                }else if(position == 1) {
                    mCurrentSearchStartTime = "12:00";
                    mCurrentSearchEndTime = "16:00";
                }else if(position == 2) {
                    mCurrentSearchStartTime = "16:00";
                    mCurrentSearchEndTime = "20:00";
                }else if (position == 3) {
                    dialogUtil.getWheelDialog(getActivity(), LWheelDialog.LWheelDialogType.ALL, null, new LWheelDialog.OnCheckedListener() {
                        @Override
                        public void onCheckedClicked(String time) {
                            ToastUtils.showShorToast("time:" + time);
                            mTimeAdapter.setCustomTime(time);
                            String[] times = time.split("-");
                            mCurrentSearchStartTime = times[0];
                            mCurrentSearchEndTime = times[1];
                        }
                    });
                }
            }
        });

        mRvRoom = (RecyclerView) view.findViewById(R.id.rv_room);
        mGridLayoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false);
        mRvRoom.setLayoutManager(mGridLayoutManager);

        getRoomsInfo();
        getTimeInfo();

        onClickListener();

        mRoomAdapter = new SearchRoomAdapter(getActivity(), mRoomsList);
        mRvRoom.setAdapter(mRoomAdapter);
        mRvRoom.addItemDecoration(new SpaceItemDecoration(DensityUtil.dip2px(getActivity(), 3)));
        mCurrentSearchDay = mSessionDaysList.get(mCurrentTimePosition);

        mRoomAdapter.setOnItemClickListener(new SearchRoomAdapter.OnItemClickListener() {
            @Override
            public void doOnItemClick(View v, SearchRoomBean data) {
                if (data == null) {
                    mCurrentSearchRoom = "";
                } else {
                    mCurrentSearchRoom = data.getRoomId() + "";
                }
            }
        });

        return view;
    }

    private void onClickListener() {
        mIvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRoomAdapter.resetSearch();
                mTimeAdapter.resetSearch();
                mTvCurrentTime.setText(mSessionDaysList.get(mCurrentTimePosition = 0).subSequence(5, 10));

                //重置搜索条件
                mCurrentSearchDay = mSessionDaysList.get(mCurrentTimePosition);
                mCurrentSearchRoom = "";
                mCurrentSearchEndTime = "";
                mCurrentSearchEndTime = "";
            }
        });

        mIvLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentTimePosition == mSessionDaysList.size() - 1) {
                    return;
                }
                mTvCurrentTime.setText(mSessionDaysList.get(++mCurrentTimePosition).subSequence(5, 10));
                mCurrentSearchDay = mSessionDaysList.get(mCurrentTimePosition);
                setArrowColor(mCurrentTimePosition);
            }
        });

        mIvPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentTimePosition == 0) {
                    return;
                }
                mTvCurrentTime.setText(mSessionDaysList.get(--mCurrentTimePosition).subSequence(5, 10));
                mCurrentSearchDay = mSessionDaysList.get(mCurrentTimePosition);
                setArrowColor(mCurrentTimePosition);
            }
        });

        mTvStartSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearchQuery(mCurrentSearchDay, mCurrentSearchRoom, mCurrentSearchStartTime, mCurrentSearchEndTime);
            }
        });
    }

    /**
     * 获取所有的房间
     */
    private void getRoomsInfo() {
        DbAdapter db = DbAdapter.getInstance();
        db.open();
        String sql = "select * from " + ConferenceTables.TABLE_INCONGRESS_CLASS;
        mRoomsList = ConferenceGetData.getClassesList(db, sql);
        db.close();
    }

    /**
     * 获取所有的时间
     */
    private void getTimeInfo() {
        DbAdapter ada = DbAdapter.getInstance();
        ada.open();
        mSessionDaysList = ada.fetchRawGroupList(
                ConferenceTables.TABLE_INCONGRESS_SESSION,
                ConferenceTableField.SESSION_SESSIONDAY,
                ConferenceTableField.SESSION_SESSIONDAY);
        ada.close();

        mTvCurrentTime.setText(mSessionDaysList.get(0).subSequence(5, 10));
    }

    private void setArrowColor(int position) {
        if (position == 0) {
            mIvPrev.setImageResource(R.drawable.left_arrow);
            mIvLast.setImageResource(R.drawable.right_arrow_clickable);
        } else if (position == (mSessionDaysList.size() - 1)) {
            mIvPrev.setImageResource(R.drawable.left_arrow_clickable);
            mIvLast.setImageResource(R.drawable.right_arrow);
        } else {
            mIvPrev.setImageResource(R.drawable.left_arrow_clickable);
            mIvLast.setImageResource(R.drawable.right_arrow_clickable);
        }
    }

    /**
     * 根据查询条件进行查询
     */
    private void doSearchQuery(String searchDay, String searchRoom, String searchStartTime, String searchEndTime) {
        String sql = "select * from " + ConferenceTables.TABLE_INCONGRESS_SESSION + " where 1=1 ";

        if(!StringUtils.isEmpty(searchDay)) {
            sql = sql + " and " + ConferenceTableField.SESSION_SESSIONDAY + " = '" + searchDay + "'";
        }

        if(!StringUtils.isEmpty(searchRoom)) {
            sql = sql + " and " + ConferenceTableField.SESSION_CLASSESID + " = '" + searchRoom + "'";
        }

        if(!StringUtils.isEmpty(searchStartTime)) {
            sql = sql + " and " + ConferenceTableField.SESSION_STARTTIME + " >= '" + searchStartTime + "'";
        }

        if(!StringUtils.isEmpty(searchEndTime)) {
            sql = sql + " and " + ConferenceTableField.SESSION_ENDTIME + " <= '" + searchEndTime + "'";
        }

        //获取查询到的数据集合
        DbAdapter ada = DbAdapter.getInstance();
        ada.open();
        List<SessionBean> sessionList = ConferenceGetData.getSessionList(ada, sql);
        ada.close();

        //获取查询到会议数据
        List<MeetingBean>  meetingBeans = new ArrayList<>();

        if(sessionList.size()>0) {
            //查询所有的session下面的meeting数据
            for(int i=0; i<sessionList.size(); i++) {
                SessionBean bean = sessionList.get(i);
                String sqlForMeeting = "select * from " + ConferenceTables.TABLE_INCONGRESS_MEETING + " where " + ConferenceTableField.MEETING_SESSIONGROUPID + " = " + bean.getSessionGroupId();
                //获取查询到的数据集合
                DbAdapter adaTemp = DbAdapter.getInstance();
                adaTemp.open();
                meetingBeans.addAll(ConferenceGetData.getMeetingList(ada, sqlForMeeting));
                adaTemp.close();
            }
        }

        List<ClassesBean> classesBeans = new ArrayList<>();
        String sqlRoom = "select * from " + ConferenceTables.TABLE_INCONGRESS_CLASS;
        DbAdapter adaRoom = DbAdapter.getInstance();
        adaRoom.open();
        classesBeans.addAll(ConferenceGetData.getClassesList(adaRoom, sqlRoom));
        adaRoom.close();

        SearchResultActivity.startSearchResultActivity(getActivity(),sessionList, meetingBeans, classesBeans, sessionList.size()+"");
    }
}
