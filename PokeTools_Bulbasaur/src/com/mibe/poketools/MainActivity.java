package com.mibe.poketools;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
//import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
ポケモン対戦用の計算ツール
メモ
	個体登録->パーティ登録から攻防のダメージを試算する
	対戦記録を保存する
		対戦相手の情報をカメラから取得する
	各種データはGoogleDocsのスプレッドシートで管理する
		編集部分は当分Webで済ませる
1. 対戦
	チーム選択
	ルール，形式選択
		シングル，ダブル，トリプル，ローテ，シューター
		フリー，ローテ，大会，その他
	対戦相手の情報入力 ※ そのうちカメラを使った取得を実装する
		トレーナー名
		星の数
		スコア（勝利数orレーティング）
		出身地
	試合開始ボタン
2. 個人データ ※そのうち編集機能も追加する
	エイリアス
	個体		: 登録名，種族名，性別，性格，個体値，努力値，特性，技1234，メモ欄
	チーム		: チーム名，メモ欄，（登録名[改行]道具）×6匹
	対戦ログ	: ※後で考える
	人物メモ	: ※後で考える
3. 環境データ ※そのうち編集機能も追加する
	汎用的な努力値調整パターン
	種族別データベース
		技		: 個数制限なし
		道具	: 個数制限なし
		特性	: 1つだけ指定
		調整	: 個数制限なし
	夢特性解禁状況
4. 一般データ ※閲覧だけでOK
	種族
	タイプ
	技
	特性
	道具
5. 設定 ※メニューボタンに移動した
	使用するGoogleアカウント名
	ホームディレクトリのパス

 * 	
 * @author mibe
 *
 */
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	private static final String TAG = "MainActivity";

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
		.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	// オプションメニューが表示される度に呼び出されます
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d(TAG, "onPrepareOptionsMenu()");
		menu.findItem(R.id.menu_help).setEnabled(false);
		return super.onPrepareOptionsMenu(menu);
	}

	// オプションメニューアイテムが選択された時に呼び出されます
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected()");
		switch(item.getItemId()){
		default:
			return super.onOptionsItemSelected(item);
		case R.id.menu_account:
		case R.id.menu_homeDir:
		case R.id.menu_help:
			Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
			return true;
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			default:return null;
			case 0: return getString(R.string.tab_name_battle);
			case 1: return getString(R.string.tab_name_user);
			case 2: return getString(R.string.tab_name_environment);
			case 3: return getString(R.string.tab_name_common);
			}
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			// レイアウトIDの格納場所
			int layoutID = 0;

			// 表示するタブのID
			int tabID = getArguments().getInt(ARG_SECTION_NUMBER);

			// タブによってレイアウトIDを変更する
			switch(tabID){
			default:
				// 該当しないタブの時，ダミー用レイアウトを指定する
				layoutID = 0;
				break;
			}
			// レイアウトIDが不正な時，ダミーを表示する
			if(layoutID == 0){

				Log.d(TAG, "tabID = 0");

				TextView textView = new TextView(getActivity());
				textView.setGravity(Gravity.CENTER);
				Bundle args = getArguments();
				textView.setText(Integer.toString(args.getInt(ARG_SECTION_NUMBER)));
				return textView;
			}

			// 指定したレイアウトを取得する
			View view = inflater.inflate(layoutID, container, false);

			// レイアウト内部の設定を行う
			setTabLayout(view, tabID);

			return view;
		}

		// 指定されたレイアウトの内部を設定する
		private void setTabLayout(View view, int tabID){

			Log.d(TAG, "setTabLayout(): tabID = ".concat(String.valueOf(tabID)));
		}
	}

}
