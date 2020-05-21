import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.restaction.RoleAction;

import javax.security.auth.login.LoginException;
import javax.xml.soap.Text;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends ListenerAdapter {
    public static void main(String[] args) throws LoginException {
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        String token = "?????";
        builder.setToken(token);
        builder.addEventListener(new Main());
        builder.buildAsync();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        User objUser = event.getAuthor();
        String str = event.getMessage().getContentRaw();
        String[] strs = str.split(" ");
        List<Member> users = event.getGuild().getMembers();


        if(event.getAuthor().isBot()){
            System.out.println("[INFO] We recived a message from " + event.getAuthor().getName() + ": " +
                    event.getMessage().getContentDisplay() + " [IGNORED: command by bot]");
            return;
        }else{
            System.out.println("We recived a message from " + event.getAuthor().getName() + ": " +
                    event.getMessage().getContentDisplay());
        }

        /*
        if (str.equals("getHistory")) {
            //event.getChannel().sendMessage("Bonjour!").queue();
            //System.out.println("Hello");
            try {
                List<Message> msgs = autorolebotFuncs.getMessages(event.getTextChannel(),100);
                System.out.println(msgs.size());
                for(Message e : msgs){
                    if(!e.getContentRaw().equals("getHistory")) {
                        event.getChannel().sendMessage(e.getContentRaw()).queue();

                    }
                }
            } catch (RateLimitedException e) {
                //e.printStackTrace();
                event.getChannel().sendMessage("Failed").queue();
            }
        }
         */

        if (str.equals("F")) {
            event.getChannel().sendMessage("F!!").queue();
        }

        //commands_start
        try {
            if (strs[0].equals("/modify")) {

                event.getChannel().sendMessage("Processing command...").queue();
                if (autorolebotFuncs.getIfhasRole("Role/Bot_Management", event.getAuthor(), guild)) {

                    if (strs[1].equals("role")) {

                        Member foundMember = autorolebotFuncs.getIfMemberExists(strs[3], guild);
                        String sub = strs[2];

                        if (sub.equals("add")) {

                            autorolebotCommands.modify_role_add(foundMember, guild, event.getChannel(), strs);
                        } else if (sub.equals("remove")) {

                            autorolebotCommands.modify_role_remove(foundMember, guild, event.getChannel(), strs[4],strs);
                        } else if (sub.equals("switch")) {

                            autorolebotCommands.modify_role_switch(foundMember, guild, event.getChannel(), strs[4], strs[5]);
                        } else {
                            event.getChannel().sendMessage("The command does not exist.").queue();
                        }
                    }
                } else {
                    event.getChannel().sendMessage("You don't have permission to run this command!").queue();
                }

            }
        }catch(ArrayIndexOutOfBoundsException e){
            event.getChannel().sendMessage("The command is not valid.").queue();

        }
        //commands_end


    }

}

class autorolebotFuncs{
    public static boolean getIfhasRole(String role, User user, Guild guild){
        for(Role r:guild.getMember(user).getRoles()){
            if(r.getName().equals(role)){
                return true;
            }
        }
        return false;
    }

    public static Member getIfMemberExists(String name,Guild guild){
        boolean foundMember = false;
        Member us = null;
        for (Member u : guild.getMembers()) {
            String nick = u.getEffectiveName();
            //System.out.println(nick);
            if (nick.equals(name)) {
                foundMember = true;
                us = u;
                return us;
            }
        }
        return null;
    }

    public static boolean getIfRoleExists(String role, Guild guild){
        for(Role r:guild.getRoles()){
            if(r.getName().equals(role)){
                return true;
            }
        }
        return false;
    }

    public static List<Message> getMessages(TextChannel c, int amount) throws RateLimitedException {
        List<Message> messages = new ArrayList<Message>();
        MessageHistory history = c.getHistory();


        while (amount > 0) {
            int numToRetrieve = amount;

            if (amount > 100) {
                numToRetrieve = 100;
            }

            List<Message> retrieved = history.retrievePast(numToRetrieve).complete();

            if (retrieved == null) {
                break;
            }

            messages.addAll(retrieved);
            amount -= numToRetrieve;
        }

        return messages;
    }

    public static String[] returnOptions(String[] command, int commandScope){
        String[] options = new String[command.length - commandScope];
        for(int i = 0; i < command.length - commandScope; i++){
            options[i] = command[i+commandScope];
            System.out.println(command[i+commandScope]);
        }

        return options;

    }

    public static boolean ifExists(String[] list, String str){
        for(String s:list){
            if(s.equals(str)){
                return true;
            }
        }
        return false;
    }

    public static List<Member> returnMembersWithRole(Guild guild, Role role){
        List<Member> membersReturn = new ArrayList<Member>();

        for(Member m: guild.getMembers()){
            if(m.getRoles().contains(role)){
                membersReturn.add(m);
                continue;
            }
        }
        return membersReturn;

    }

    public static Member[] convertListToArray(List<Member> mems){
        Member[] m = new Member[mems.size()];
        for(int i = 0; i < mems.size();i++){
            m[i] = mems.get(i);
        }
        return m;
    }


}

class autorolebotCommands{
    public static void modify_role_add(Member member,Guild guild, MessageChannel msg,String[] command){
        if(autorolebotFuncs.ifExists(autorolebotFuncs.returnOptions(command,5),"-r")) {
            if(autorolebotFuncs.getIfRoleExists(command[4],guild)) {
                for (Member mm : autorolebotFuncs.convertListToArray(autorolebotFuncs.returnMembersWithRole(guild, guild.getRolesByName(command[3], true).get(0)))) {
                    guild.getController().addRolesToMember(mm, guild.
                            getRolesByName(command[4], true)).queue();
                    msg.sendMessage("Added role " + command[4] + " to " + mm.getEffectiveName()).queue();
                }
            }else{
                if(autorolebotFuncs.ifExists(autorolebotFuncs.returnOptions(command, 5),"-c")){
                    //guild.getController().addRolesToMember(member, guild.getController().createRole().setName(command[4]).complete()).queue();
                    for (Member mm : autorolebotFuncs.convertListToArray(autorolebotFuncs.returnMembersWithRole(guild, guild.getRolesByName(command[3], true).get(0)))) {
                        if(!autorolebotFuncs.getIfRoleExists(command[4],guild)) {
                            guild.getController().addRolesToMember(mm, guild.getController().createRole().setName(command[4]).complete()).queue();
                        }else{
                            guild.getController().addRolesToMember(mm,guild.getRolesByName(command[4],true)).queue();
                        }
                        msg.sendMessage("Added role " + command[4] + " to " + mm.getEffectiveName()).queue();
                    }

                }else{
                    msg.sendMessage("The role does not exist. :(").queue();
                }
            }
        }else {
            if (!Objects.equals(member, null)) {
                if (autorolebotFuncs.getIfRoleExists(command[4], guild)) {

                    guild.getController().addRolesToMember(member, guild.
                            getRolesByName(command[4], true)).queue();
                    msg.sendMessage("Added role " + command[4] + " to " + member.getEffectiveName()).queue();

                } else {
                    if (autorolebotFuncs.ifExists(autorolebotFuncs.returnOptions(command, 5),"-c")) {
                        //RoleAction role = guild.getController().createRole();
                        //role.setName(command[4]).queue();
                        System.out.println("hello");


                        guild.getController().addRolesToMember(member, guild.getController().createRole().setName(command[4]).complete()).queue();
                        msg.sendMessage("Added role " + command[4] + " to " + member.getEffectiveName()).queue();

                    } else {
                        msg.sendMessage("The role does not exist. :(").queue();
                    }
                }
            } else {
                msg.sendMessage("I don't found this user!").queue();
            }
        }
    }

    public static void modify_role_remove(Member member,Guild guild, MessageChannel msg,String roleName,String[] command){
        if(autorolebotFuncs.ifExists(autorolebotFuncs.returnOptions(command,5),"-r")){
            if(autorolebotFuncs.getIfRoleExists(command[4],guild)){
                for (Member mm : autorolebotFuncs.convertListToArray(autorolebotFuncs.returnMembersWithRole(guild, guild.getRolesByName(command[3], true).get(0)))) {
                    guild.getController().removeRolesFromMember(mm, guild.
                            getRolesByName(command[4], true)).queue();
                    msg.sendMessage("Removed role " + command[4] + " to " + mm.getEffectiveName()).queue();
                }

            }else{
                msg.sendMessage("The role doesn't exist.").queue();
            }

        }else {
            if (!Objects.equals(member, null)) {
                if (autorolebotFuncs.getIfRoleExists(roleName, guild)) {
                    guild.getController().removeRolesFromMember(member, guild.
                            getRolesByName(roleName, true)).queue();
                    msg.sendMessage("Removed role " + roleName + " to " + member.getEffectiveName()).queue();
                } else {
                    msg.sendMessage("The role does not exist. :(").queue();
                }
            } else {
                msg.sendMessage("I don't found this user!").queue();
            }
        }
    }

    public static void modify_role_switch(Member member,Guild guild, MessageChannel msg, String rl1, String rl2){
        if(!Objects.equals(member,null)){

            if(autorolebotFuncs.getIfRoleExists(rl1,guild)){

                if(autorolebotFuncs.getIfRoleExists(rl2,guild)){
                    //guild.getController().addRolesToMember(member,guild.getRolesByName(rl1,false)).queue();


                    guild.getController().modifyMemberRoles(member,guild.
                            getRolesByName(rl1, true),guild.
                            getRolesByName(rl2, true)).queue();



                    msg.sendMessage("Successfully added " + rl1 + " role and removed " + rl2 + " role.").queue();

                }else{
                    msg.sendMessage("The role " + rl2 + " don't exist.").queue();
                    return;
                }
            }else{
                msg.sendMessage("The role " + rl1 + " don't exist.").queue();
                return;
            }
        }else{
            msg.sendMessage("I don't found this user!").queue();
            return;
        }
    }
}

