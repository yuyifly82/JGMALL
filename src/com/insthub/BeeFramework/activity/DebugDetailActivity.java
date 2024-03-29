package com.insthub.BeeFramework.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.insthub.jgmall.R;
import com.insthub.BeeFramework.model.DebugMessageModel;

/*
 *	 ______    ______    ______
 *	/\  __ \  /\  ___\  /\  ___\
 *	\ \  __<  \ \  __\_ \ \  __\_
 *	 \ \_____\ \ \_____\ \ \_____\
 *	  \/_____/  \/_____/  \/_____/
 *
 *
 *	Copyright (c) 2013-2014, {Bee} open source community
 *	http://www.bee-framework.com
 *
 *
 *	Permission is hereby granted, free of charge, to any person obtaining a
 *	copy of this software and associated documentation files (the "Software"),
 *	to deal in the Software without restriction, including without limitation
 *	the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *	and/or sell copies of the Software, and to permit persons to whom the
 *	Software is furnished to do so, subject to the following conditions:
 *
 *	The above copyright notice and this permission notice shall be included in
 *	all copies or substantial portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *	FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *	IN THE SOFTWARE.
 */

public class DebugDetailActivity extends BaseActivity {
	
	private TextView time;
	private TextView message;
	private TextView request;
	private TextView response;
	private TextView netSize;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug_message_detail);
		
		Intent intent = getIntent();
		int position = intent.getIntExtra("position", 0);
		
		time = (TextView) findViewById(R.id.debug_detail_time);
		message = (TextView) findViewById(R.id.debug_detail_message);
		request = (TextView) findViewById(R.id.debug_detail_request);
		response = (TextView) findViewById(R.id.debug_detail_response);
		netSize = (TextView) findViewById(R.id.debug_detail_netSize);
		
		time.setText(DebugMessageModel.sendingmessageList.get(position).startTime);
		message.setText(DebugMessageModel.sendingmessageList.get(position).message);
		request.setText(DebugMessageModel.sendingmessageList.get(position).requset);
		response.setText(DebugMessageModel.sendingmessageList.get(position).response);
		netSize.setText(DebugMessageModel.sendingmessageList.get(position).netSize);
		
	}

}
