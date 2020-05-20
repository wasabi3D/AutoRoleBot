import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.List;

public class Main extends ListenerAdapter {
    public static void main(String[] args) throws LoginException {
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        String token = "????????";
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


        System.out.println("We recived a message from " + event.getAuthor().getName() + ": " +
                event.getMessage().getContentDisplay());

        if (str.equals("bjr")) {
            event.getChannel().sendMessage("Bonjour!").queue();
        }

        if (str.equals("F")) {
            event.getChannel().sendMessage("F!!").queue();
        }

        if (str.equals("!give test role")) {
            //event.getChannel().sendMessage("F!!").queue();
            Role rl = guild.getRolesByName("testRole", false).get(0);
            guild.getController().addRolesToMember(guild.getMember(objUser), rl).queue();
            event.getChannel().sendMessage("Added role testRole to " + objUser.getName()).queue();
        }



        if (strs[0].equals("/modify")) {

            if (autorolebotFuncs.getIfhasRole("RoleManagement",event.getAuthor(),guild)) {

                if (strs[1].equals("role")) {

                    Member foundMember = autorolebotFuncs.getIfMemberExists(strs[3],guild);
                    if (strs[2].equals("add")) {

                        autorolebotCommands.modify_role_add(foundMember,guild,event.getChannel(),strs[4]);
                    } else if (strs[2].equals("remove")) {

                        autorolebotCommands.modify_role_remove(foundMember,guild,event.getChannel(),strs[4]);
                    }else{
                        event.getChannel().sendMessage("The command does not exist.");
                    }
                }
            } else {
                event.getChannel().sendMessage("You don't have permission to run this command!").queue();
            }

        }


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


}

class autorolebotCommands{
    public static void modify_role_add(Member member,Guild guild, MessageChannel msg,String roleName){
        if (!member.equals(null)) {
            if(autorolebotFuncs.getIfRoleExists(roleName,guild)) {
                guild.getController().addRolesToMember(member, guild.
                        getRolesByName(roleName, true)).queue();
                msg.sendMessage("Added role " + roleName + " to " + member.getNickname()).queue();
            }else{
                msg.sendMessage("The role does not exist. :(").queue();
            }
        } else {
            msg.sendMessage("I don't found this user!").queue();
        }
    }

    public static void modify_role_remove(Member member,Guild guild, MessageChannel msg,String roleName){
        if (!member.equals(null)) {
            if(autorolebotFuncs.getIfRoleExists(roleName,guild)) {
                guild.getController().removeRolesFromMember(member, guild.
                        getRolesByName(roleName, true)).queue();
                msg.sendMessage("Removed role " + roleName + " to " + member.getNickname()).queue();
            }else{
                msg.sendMessage("The role does not exist. :(").queue();
            }
        } else {
            msg.sendMessage("I don't found this user!").queue();
        }
    }
}

