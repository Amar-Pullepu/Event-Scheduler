/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eventscheduler;

import java.util.*;

/**
 *
 * @author amare
 */
class Player {
    private int playerId, points;
    private String skill, level;
    public void set_playerId(int pId){
        playerId = pId;
    }
    public void set_points(int pnts){
        points = pnts;
    }
    public void set_skill(String skl){
        skill = skl;
    }
    public void set_level(String lvl){
        level = lvl;
    }
    public int get_playerId(){
        return playerId;
    }
    public int get_points(){
        return points;
    }
    public String get_skill(){
        return skill;
    }
    public String get_level(){
        return level;
    }
    Player(int pId, int pnts, String skl, String lvl){
        playerId = pId;
        points = pnts;
        skill = skl;
        level = lvl;
    }
}

public class Solution {
    public static int findPointsForGivenSkill(Player[] players, String skl){
        int pnts = 0;
        for(Player player: players){
            if(player.get_skill().equalsIgnoreCase(skl)){
                pnts+=player.get_points();
            }
        }
        return pnts;
    }
    public static Player getPlayerBasedOnLevel(String lvl, String skl, Player[] players){
        for(Player player: players){
            if(player.get_level().equalsIgnoreCase(lvl) && player.get_skill().equalsIgnoreCase(skl) && player.get_points() >= 20){
                return player;
            }
        }
        return null;
    }
    public static void main(String Args){
        Scanner sc = new Scanner(System.in);
        Player[] players = new Player[4];
        for(int i=0; i<4; i++){
            int pId = Integer.parseInt(sc.nextLine());
            String skl = sc.nextLine();
            String lvl = sc.nextLine();
            int pnts = Integer.parseInt(sc.nextLine());
            players[i] = new Player(pId, pnts, skl, lvl);
        }
        String skl = sc.nextLine();
        String lvl = sc.nextLine();
        
        int pntsForSkl = findPointsForGivenSkill(players, skl);
        if(pntsForSkl == 0){
            System.out.println("The given Skill is not available");
        }
        else{
            System.out.println(pntsForSkl);
        }
        Player matchedPlr = getPlayerBasedOnLevel(lvl, skl, players);
        if(matchedPlr == null){
            System.out.println("No player is available with specified level, skill and eligibility points");
        }
        else{
            System.out.println(matchedPlr.get_playerId());
        }
    }
}
