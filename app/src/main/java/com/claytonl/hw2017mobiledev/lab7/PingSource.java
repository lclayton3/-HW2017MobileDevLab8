package com.claytonl.hw2017mobiledev.lab7;

/**
 * Created by claytonl on 8/7/17.
 */


import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PingSource {

        public interface PingListener {
            void onPingsReceived(List<Ping> pingList);
        }

        private static PingSource sNewsSource;

        private Context mContext;

        public static PingSource get(Context context) {
            if (sNewsSource == null) {
                sNewsSource = new PingSource(context);
            }
            return sNewsSource;
        }

        private PingSource(Context context) {
            mContext = context;
        }

        // Firebase methods for you to implement.

        public void getPings(final PingListener pingListener) {
            DatabaseReference pingsRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference getRef = pingsRef.child("pings");
            Query last50PingsQuery = getRef.limitToLast(50);
            final List<Ping> pingList = new ArrayList<Ping>();
            last50PingsQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> pingsSnapshots = dataSnapshot.getChildren();
                    for(DataSnapshot pingSnap : pingsSnapshots){
                        Ping Pings = new Ping(pingSnap);
                        pingList.add(Pings);
                    }

                    pingListener.onPingsReceived(pingList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }

        public void getPingsForUserId(String userId, final PingListener pingListener) {
            DatabaseReference pingsRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference getRef = pingsRef.child("pings");
            Query userQuery = getRef.orderByChild("userId").equalTo(userId).limitToLast(50);
            userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Ping> pingList = new ArrayList<Ping>();
                    Iterable<DataSnapshot> pingsSnapshots = dataSnapshot.getChildren();
                    for(DataSnapshot pingSnap : pingsSnapshots){
                        Ping Pings = new Ping(pingSnap);
                        pingList.add(Pings);
                    }

                    pingListener.onPingsReceived(pingList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




        }

        public void sendPing(Ping ping) {
            DatabaseReference pingsRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference newPingRef = pingsRef.push();
            Map<String, Object> pingMapVal = new HashMap<String,Object>();
            pingMapVal.put ("userId", ping.getUserId());
            pingMapVal.put ("userName", ping.getUserName());
            pingMapVal.put("timestamp", ServerValue.TIMESTAMP);

            newPingRef.setValue(pingMapVal);
        }
    }


