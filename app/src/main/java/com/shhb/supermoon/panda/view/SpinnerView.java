package com.shhb.supermoon.panda.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shhb.supermoon.panda.R;
import com.shhb.supermoon.panda.tools.BaseTools;

import java.util.List;

/**
 * Created by superMoon on 2017/8/3.
 */
public class SpinnerView extends TextView {
    private OnSpinnerItemClickListener mOnSpinnerItemClickListener;
    private int currentSelectedItemPosition = 1;
    private IosSpinnerAdapter iosAdapter;
    private List<String> datas;
    private PopupWindow mPopWindow;

    public SpinnerView(Context context) {
        super(context);
        initView((Activity) context);
    }

    public SpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (context instanceof Activity) {

            initView((Activity) context);
        } else {

        }
    }

    public SpinnerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView((Activity) context);
    }

    /**
     * 设置item 点击事件
     *
     * @param listener
     */
    public void setOnSpinnerItemClickListener(OnSpinnerItemClickListener listener) {
        this.mOnSpinnerItemClickListener = listener;
    }

    /**
     * 获取当前选中的item
     *
     * @return
     */
    public int getSelectedItemPosition() {
        return currentSelectedItemPosition;
    }

    /**
     * 初始化下拉框
     *
     * @param context
     */
    private void initView(Activity context) {
        Drawable drawable = getResources().getDrawable(R.mipmap.down2);
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        setCompoundDrawables(null, null, drawable, null);
    }

    /**
     * 设置数据源
     *
     * @param datas
     */
    private void setData(List<String> datas) {
        this.datas = datas;
    }

    /**
     * 显示window
     */
    public void showWindow() {
        Drawable drawable = getResources().getDrawable(R.mipmap.color_up);
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        setCompoundDrawables(null, null, drawable, null);
        mPopWindow.showAsDropDown(this);
    }

    /**
     * 初始化列表window
     *
     * @param context 上下文 必须是activity
     * @param list    数据源
     */
    public void init(final Activity context, final List<String> list) {
        setData(list);
        setGravity(Gravity.CENTER);
        setText(datas.get(0));
        View view = View.inflate(context, R.layout.spinner_lists, null);
        final ListView listView = (ListView) view.findViewById(R.id.pop_list);
        iosAdapter = new IosSpinnerAdapter(context);
        listView.setAdapter(iosAdapter);

        mPopWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SpinnerView.this.setText(datas.get(position));
                currentSelectedItemPosition = position + 1;
                iosAdapter.notifyDataSetChanged();
                mPopWindow.dismiss();
                if (mOnSpinnerItemClickListener != null) {
                    mOnSpinnerItemClickListener.OnSpinnerItemClick(parent, view, position, id,list);
                }
            }
        });

        mPopWindow.setBackgroundDrawable(new ColorDrawable());
        mPopWindow.setOutsideTouchable(true);// 点击外部可关闭窗口
        mPopWindow.setFocusable(true);//设置窗体可点击
        mPopWindow.setTouchable(true);
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Drawable drawable = getResources().getDrawable(R.mipmap.down2);
                /// 这一步必须要做,否则不会显示.
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                setCompoundDrawables(null, null, drawable, null);
            }
        });
    }

    /**
     * 关闭当前的window
     */
    public void closeWindow() {
        if (mPopWindow != null && mPopWindow.isShowing()) {
            mPopWindow.dismiss();
        }
    }


    class IosSpinnerAdapter extends BaseAdapter {
        private Context context;

        public IosSpinnerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.spinner_item, null);
            }
            View up_divider = convertView.findViewById(R.id.up_divider);
            TextView textView = (TextView) convertView.findViewById(R.id.type);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.isSelected);

            if (position == 0) {
                up_divider.setVisibility(View.VISIBLE);
            }
            if (position == currentSelectedItemPosition - 1) {
                imageView.setVisibility(View.VISIBLE);
                textView.setTextColor(ContextCompat.getColor(context, R.color.app_color));
            } else {
                imageView.setVisibility(View.GONE);
                textView.setTextColor(ContextCompat.getColor(context, R.color.text_color));
            }
            textView.setText(datas.get(position));
            return convertView;
        }
    }


    public interface OnSpinnerItemClickListener {
        void OnSpinnerItemClick(AdapterView<?> parent, View view, int position, long id, List<String> list);
    }
}
