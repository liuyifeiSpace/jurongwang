package com.orong.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.orong.Constant;
import com.orong.R;
import com.orong.entity.HttpDatas;
import com.orong.utils.net.NetUtils;
import com.orong.utils.net.HttpAsyncTask.TaskCallBack;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TelUsActivity extends BaseActivity {

	private TextView tvTelephone;// 客服电话
	private ImageView ivCallService;// 呼叫客服
	private EditText etOpinion;// 意见信息
	private Button btSubmit;// 确认提交

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tel_us);

		initivReabck(this);
		setTitle(this, R.string.aboutus);

		tvTelephone = (TextView) this.findViewById(R.id.tv_telephone);
		ivCallService = (ImageView) this.findViewById(R.id.iv_call_service);
		ivCallService.setOnClickListener(this);
		etOpinion = (EditText) this.findViewById(R.id.et_opinion);
		etOpinion.addTextChangedListener(new SuggestTextWatcher());
		btSubmit = (Button) this.findViewById(R.id.bt_submit);
		btSubmit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_call_service:
			callPhone();
			break;
		case R.id.bt_submit:
			submintOpinion();
			break;
		default:
			super.onClick(v);
			break;
		}

	}

	/**
	 * 启动打电话功能
	 */
	private void callPhone() {
		String phone = tvTelephone.getText().toString();

		Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + phone));
		startActivity(intent);
	}

	private void submintOpinion() {
		String opinion = etOpinion.getText().toString().trim();
		if ("".equals(opinion)) {
			Toast.makeText(this, "意见不能为空", 0).show();
			return;
		}
		if (opinion.length() < 5) {
			Toast.makeText(this, getString(R.string.text_less_than_5), 0).show();
			return;
		}
		if (opinion.length() > 100) {
			Toast.makeText(this, getString(R.string.text_more_than_100), 0).show();
			return;
		}
		HttpDatas datas = new HttpDatas(Constant.USERAPI);
		datas.putParam("method", "ContactUs");
		datas.putParam("reason", opinion);
		NetUtils.sendRequest(datas, this, getString(R.string.uploading_message), new TaskCallBack() {

			@Override
			public int excueHttpResponse(String respondsStr) {
				int code = 0;
				try {
					JSONObject jsonObject = new JSONObject(respondsStr);
					code = jsonObject.getInt("code");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return code;
			}

			@Override
			public void beforeTask() {

			}

			@Override
			public void afterTask(int result) {
				switch (result) {
				case Constant.RESPONSE_OK:
					Toast.makeText(getApplicationContext(), getString(R.string.thanks_for_opinion), 0).show();
					break;
				case 3009:
					Toast.makeText(getApplicationContext(), getString(R.string.was_resummit), 0).show();
					break;
				default:
					showResulttoast(result, TelUsActivity.this);
					break;
				}
			}
		});

	}

	class SuggestTextWatcher implements TextWatcher {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			int length = s.length();
			if (length > 100) {
				s.delete(length - 1, length);
				length--;
				Toast.makeText(getApplicationContext(), "字数超限", 0).show();
				return;
			}
		}

	}
}
