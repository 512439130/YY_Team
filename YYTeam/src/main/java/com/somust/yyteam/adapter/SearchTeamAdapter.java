package com.somust.yyteam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.somust.yyteam.R;
import com.somust.yyteam.bean.AllTeam;
import com.somust.yyteam.bean.AllUser;

import java.util.List;

/**
 * Created by yetwish on 2015-05-11
 */

public class SearchTeamAdapter extends BaseAdapter {
    private Context context;
    private List<AllTeam> allTeams;
    private LayoutInflater inflater;
    private static final String TAG = "SearchUserAdapter:";

    public SearchTeamAdapter(Context context, List<AllTeam> teams) {
        this.context = context;
        this.allTeams = teams;
        this.inflater = LayoutInflater.from(context);



    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return allTeams.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return allTeams.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewholder = null;
        final AllTeam allTeam = allTeams.get(position);
        if (convertView == null) {
            viewholder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_search_team, null);
            viewholder.team_image = (ImageView) convertView.findViewById(R.id.team_image);

            viewholder.team_name = (TextView) convertView.findViewById(R.id.team_name);

            viewholder.team_type = (ImageView) convertView.findViewById(R.id.team_type);

            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }
        viewholder.team_image.setImageBitmap(allTeam.getTeamImage());

        viewholder.team_name.setText(allTeam.getTeamName());

        if(allTeam.getTeamType().equals("学习")){
            viewholder.team_type.setImageResource(R.mipmap.ic_team_study);
        }else if(allTeam.getTeamType().equals("公益")){
            viewholder.team_type.setImageResource(R.mipmap.ic_team_welfare);
        }else if(allTeam.getTeamType().equals("技术")){
            viewholder.team_type.setImageResource(R.mipmap.ic_team_technology);
        }

        return convertView;
    }



    class ViewHolder {
        ImageView team_image;
        TextView team_name;
        ImageView team_type;
    }
}
