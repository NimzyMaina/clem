package com.clemcreativity.clem.helper;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.clemcreativity.clem.R;
import com.clemcreativity.clem.activity.MainActivity;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 6/9/2016.
 */
public class DrawerHelper {

    private static SessionManager session;
    private static ArrayList<IDrawerItem> items;
    private static PrimaryDrawerItem item;
    private static DrawerBuilder drawerBuilder;
    private  static AccountHeader accountHeader;
    //private SessionManager session;

    public static AccountHeader getAcountHeader(Activity activity){

        session = new SessionManager(activity.getApplicationContext());

        String name = session.getKeyFname() + " " + session.getKeyLname();
        String email = session.getKeyEmail();
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(name).withEmail(email).withIcon(R.drawable.profile)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        return headerResult;
    }

    public static ArrayList<IDrawerItem> getPrimaryDrawerItems(){
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withName("Home").withIcon(FontAwesome.Icon.faw_github);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withName("Settings");
        items = new ArrayList<>(2);
        items.add(new PrimaryDrawerItem().withName("Home").withIcon(FontAwesome.Icon.faw_home));
        items.add(new PrimaryDrawerItem().withName("Settings").withIcon(FontAwesome.Icon.faw_cogs));
        return items;
    }

}
