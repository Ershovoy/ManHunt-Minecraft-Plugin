package me.Ersh.ManHunt;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class main extends JavaPlugin implements Listener
{
    ArrayList<String> runners = new ArrayList<>();
    ArrayList<String> hunters = new ArrayList<>();

    public enum ManHunt
    {
        CLASSIC,
        INFECTIOUS,
        SWITCH,
        JUGGERNAUT
    }

    ManHunt manhunt = ManHunt.CLASSIC;

    boolean isGameStarted = false;
    boolean isInteractBlocked = true;
    boolean fixRightClickBlock = true;

    @Override
    public void onEnable()
    {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable()
    {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (cmd.getName().equalsIgnoreCase("runner"))
        {
            if (args[0].equalsIgnoreCase("add"))
            {
                for (int i = 1; i < args.length; ++i)
                {
                    if (Bukkit.getPlayer(args[i]).isOnline() && !runners.contains(args[i]))
                    {
                        runners.add(Bukkit.getPlayer(args[i]).getName());
                    }
                }
                Bukkit.broadcastMessage(ChatColor.GOLD + runners.toString().substring(1, runners.toString().length() - 1)
                        + " now " + ChatColor.GREEN + "speedrunners.");
                return true;
            }
            else if (args[0].equalsIgnoreCase("remove"))
            {
                for (int i = 1; i < args.length; ++i)
                {
                    if (runners.contains(args[i]))
                    {
                        runners.remove(args[i]);
                    }
                }
                Bukkit.broadcastMessage(ChatColor.GOLD + runners.toString().substring(1, runners.toString().length() - 1)
                        + " now " + ChatColor.GREEN + "speedrunners.");
                return true;
            }
            return false;
        }

        if (cmd.getName().equalsIgnoreCase("hunter"))
        {
            if (args[0].equalsIgnoreCase("add"))
            {
                for (int i = 1; i < args.length; ++i)
                {
                    if (Bukkit.getPlayer(args[i]).isOnline() && !hunters.contains(args[i]))
                    {
                        hunters.add(Bukkit.getPlayer(args[i]).getName());
                    }
                }
                Bukkit.broadcastMessage(ChatColor.GOLD + hunters.toString().substring(1, hunters.toString().length() - 1)
                        + " now " + ChatColor.RED + "hunters.");
                return true;
            }
            else if (args[0].equalsIgnoreCase("remove"))
            {
                for (int i = 1; i < args.length; ++i)
                {
                    if (hunters.contains(args[i]))
                    {
                        hunters.remove(Bukkit.getPlayer(args[i]).getName());
                    }
                }
                Bukkit.broadcastMessage(ChatColor.GOLD + hunters.toString().substring(1, hunters.toString().length() - 1)
                        + " now " + ChatColor.RED + "hunters.");
                return true;
            }
            return false;
        }

        if (cmd.getName().equalsIgnoreCase("start"))
        {
            if (!isGameStarted)
            {
                Bukkit.getWorlds().get(0).setDifficulty(Difficulty.EASY);
                Bukkit.getWorlds().get(0).setFullTime(1000);
                Bukkit.getWorlds().get(0).getWorldBorder().setSize(6000);
                Bukkit.getWorlds().get(1).getWorldBorder().setSize(800);

                for (Player player : Bukkit.getOnlinePlayers())
                {
                    player.setGameMode(GameMode.SURVIVAL);
                    player.setHealth(0);
                }

                isInteractBlocked = false;

                Bukkit.getScheduler().runTaskLater(this, new Runnable()
                {
                    @Override
                    public void run()
                    {
                        isGameStarted = true;
                    }
                }, 200);
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "Game already stated!");
            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("startWithOutDying"))
        {
            for (Player player : Bukkit.getOnlinePlayers())
            {
                player.setGameMode(GameMode.SURVIVAL);
            }
            isInteractBlocked = false;
            isGameStarted = true;
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("manhunt"))
        {
            if (args[0].equalsIgnoreCase("classic"))
            {
                manhunt = ManHunt.CLASSIC;
                Bukkit.broadcastMessage(ChatColor.GOLD + "ManHunt classic enabled!");
                return true;
            }
            else if (args[0].equalsIgnoreCase("infectious"))
            {
                manhunt = ManHunt.INFECTIOUS;
                Bukkit.broadcastMessage(ChatColor.GOLD + "ManHunt infectious enabled!");
                return true;
            }
            return false;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args)
    {
        List<String> argument = Arrays.asList();

        if (cmd.getName().equalsIgnoreCase("runner") || cmd.getName().equalsIgnoreCase("hunter"))
        {
            argument = Arrays.asList("add", "remove");
        }
        else if (cmd.getName().equalsIgnoreCase("manhunt"))
        {
            argument = Arrays.asList("classic", "infectious");
        }

        List<String> result = new ArrayList<>();

        if (args.length == 1)
        {
            for (String s : argument)
            {
                if (s.toLowerCase().startsWith(args[0].toLowerCase()))
                {
                    result.add(s);
                }
            }
            return result;
        }
        return null;
    }

    @EventHandler
    public void clickOnCompass(PlayerInteractEvent event)
    {
        if (hunters.contains(event.getPlayer().getName()))
        {
            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            {
                if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && fixRightClickBlock)
                {
                    fixRightClickBlock = false;
                    return;
                }
                else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !fixRightClickBlock)
                {
                    fixRightClickBlock = true;
                }

                Player hunter = event.getPlayer();
                if ((hunter.getInventory().getItemInMainHand().getType().equals(Material.COMPASS) && hunter.getInventory().getItemInMainHand().containsEnchantment(Enchantment.DURABILITY))
                        || (hunter.getInventory().getItemInOffHand().getType().equals(Material.COMPASS) && hunter.getInventory().getItemInOffHand().containsEnchantment(Enchantment.DURABILITY)))
                {
                    for (int i = 0; i < runners.size(); i++)
                    {
                        Player runner = Bukkit.getPlayer(runners.get(i));
                        if (runner.isOnline() && runner.getWorld() == hunter.getWorld())
                        {
                            Location loc_runner = runner.getLocation();
                            Location loc_hunter = hunter.getLocation();

                            String str_runner = runners.get(i);
                            for (int j = i + 1; j < runners.size(); j++)
                            {
                                Player next_runner = Bukkit.getPlayer(runners.get(j));
                                if (next_runner.isOnline() && next_runner.getWorld() == hunter.getWorld())
                                {
                                    Location loc_next_runner = next_runner.getLocation();
                                    if (loc_hunter.distance(loc_runner) < loc_hunter.distance(loc_next_runner))
                                    {
                                        loc_runner = loc_next_runner;
                                        str_runner = runners.get(j);
                                    }
                                }
                            }

                            CompassMeta compassMeta;
                            if (hunter.getInventory().getItemInMainHand().getType().equals(Material.COMPASS))
                            {
                                compassMeta = (CompassMeta) hunter.getInventory().getItemInMainHand().getItemMeta();
                            }
                            else
                            {
                                compassMeta = (CompassMeta) hunter.getInventory().getItemInOffHand().getItemMeta();
                            }

                            compassMeta.setLodestone(loc_runner);
                            compassMeta.setLodestoneTracked(false);

                            char yDirection;
                            if (loc_runner.getY() < loc_hunter.getY())
                            {
                                yDirection = '↓';
                            }
                            else
                            {
                                yDirection = '↑';
                            }

                            if (compassMeta.isUnbreakable())
                            {
                                compassMeta.setUnbreakable(false);
                                compassMeta.setDisplayName(ChatColor.GREEN + "" + yDirection + " " + str_runner + " " + yDirection);
                            }
                            else if (!compassMeta.isUnbreakable())
                            {
                                compassMeta.setUnbreakable(true);
                                compassMeta.setDisplayName(ChatColor.GREEN + " " + yDirection + " " + str_runner + " " + yDirection + " ");
                            }

                            if (hunter.getInventory().getItemInMainHand().getType().equals(Material.COMPASS))
                            {
                                hunter.getInventory().getItemInMainHand().setItemMeta(compassMeta);
                            }
                            else
                            {
                                hunter.getInventory().getItemInOffHand().setItemMeta(compassMeta);
                            }
                            return;
                        }
                    }
                    hunter.sendMessage(ChatColor.BLUE + "No player to track!");
                }
            }
        }
    }

    @EventHandler
    public void death(PlayerDeathEvent event)
    {
        if (isGameStarted && runners.contains(event.getEntity().getPlayer().getName()))
        {
            runners.remove(event.getEntity().getPlayer().getName());

            if (manhunt.equals(ManHunt.INFECTIOUS))
            {
                hunters.add(event.getEntity().getPlayer().getName());
            }
        }

        if (hunters.contains(event.getEntity().getPlayer().getName()))
        {
            InventoryView inventory = event.getEntity().getPlayer().getOpenInventory();

            for (int i = 0; i <= 45; i++)
            {
                if (inventory.getItem(i).getType().equals(Material.COMPASS)
                        && inventory.getItem(i).containsEnchantment(Enchantment.DURABILITY))
                {
                    inventory.getItem(i).setAmount(0);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent event)
    {
        if (!isInteractBlocked && hunters.contains(event.getPlayer().getName()))
        {
            ItemStack compass = new ItemStack(Material.COMPASS);
            compass.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
            event.getPlayer().getInventory().addItem(compass);
        }
        else if (isGameStarted)
        {
            Bukkit.getScheduler().runTaskLater(this, new Runnable()
            {
                @Override
                public void run()
                {
                    event.getPlayer().setGameMode(GameMode.SPECTATOR);
                }
            }, 5);
        }
    }

    @EventHandler
    public void piglinBarterPerlsBoost(PiglinBarterEvent event)
    {
        if (event.getOutcome().get(0).getType().equals(Material.ENDER_PEARL))
        {
            event.getOutcome().get(0).setAmount(event.getOutcome().get(0).getAmount() + 12);
        }
    }

    @EventHandler
    public void clericTradePerlsBoost(PlayerInteractEntityEvent event)
    {
        Villager villager = (Villager) event.getRightClicked();

        if (villager.getProfession().equals(Villager.Profession.CLERIC) && villager.getVillagerLevel() == 4)
        {
            MerchantRecipe merchantRecipe = new MerchantRecipe(new ItemStack(Material.ENDER_PEARL, 1), 16);
            merchantRecipe.addIngredient(new ItemStack(Material.EMERALD, 1));
            merchantRecipe.addIngredient(new ItemStack(Material.AIR));
            merchantRecipe.setVillagerExperience(8);

            if (villager.getRecipe(6).getResult().getType().equals(Material.ENDER_PEARL))
            {
                villager.setRecipe(6, merchantRecipe);
            }
            else if (villager.getRecipe(7).getResult().getType().equals(Material.ENDER_PEARL))
            {
                villager.setRecipe(7, merchantRecipe);
            }
            else
            {
                villager.setRecipe(6, merchantRecipe);
            }
        }
    }

    @EventHandler
    public void cleticTradeExperienceBoost(VillagerAcquireTradeEvent event)
    {
        Villager villager = (Villager) event.getEntity();

        if (villager.getProfession().equals(Villager.Profession.CLERIC))
        {
            event.getRecipe().setVillagerExperience(event.getRecipe().getVillagerExperience() * 4);
        }
    }

    @EventHandler
    public void endermanPerlsBoost(EntityDeathEvent event)
    {
        if (event.getEntityType().equals(EntityType.ENDERMAN))
        {
            if (event.getDrops().isEmpty() && !event.getEntity().getWorld().getEnvironment().equals(World.Environment.THE_END))
            {
                event.getDrops().add(new ItemStack(Material.ENDER_PEARL));
            }
        }
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event)
    {
        if (isGameStarted && !(runners.contains(event.getPlayer().getName()) || hunters.contains(event.getPlayer().getName())))
        {
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
        else if (isInteractBlocked)
        {
            event.getPlayer().setGameMode(GameMode.ADVENTURE);
        }
    }

    @EventHandler
    public void canselInteractWithBlocks(PlayerInteractEvent event)
    {
        if (isInteractBlocked)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void canselDamageToEntity(EntityDamageByEntityEvent event)
    {
        if (isInteractBlocked && event.getDamager() instanceof Player && !(event.getEntity() instanceof Player))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void canselItemPickUp(EntityPickupItemEvent event)
    {
        if (isInteractBlocked && event.getEntity() instanceof Player)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void canselCompassDrop(PlayerDropItemEvent event)
    {
        if (hunters.contains(event.getPlayer().getName()))
        {
            if (event.getItemDrop().getItemStack().getType().equals(Material.COMPASS)
                    && event.getItemDrop().getItemStack().containsEnchantment(Enchantment.DURABILITY))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void canselCompassMove(InventoryClickEvent event)
    {
        if (hunters.contains(event.getWhoClicked().getName()))
        {
            if (!event.getView().getType().equals(InventoryType.CREATIVE) && !event.getView().getType().equals(InventoryType.CRAFTING))
            {
                if (event.getCurrentItem().getType().equals(Material.COMPASS) && event.isShiftClick()
                        && event.getCurrentItem().containsEnchantment(Enchantment.DURABILITY))
                {
                    event.setCancelled(true);
                }
            }

            if (!event.getClickedInventory().getType().equals(InventoryType.PLAYER))
            {
                if (event.getCursor().getType().equals(Material.COMPASS) && event.getCursor().containsEnchantment(Enchantment.DURABILITY))
                {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void canselCompassDrag(InventoryDragEvent event)
    {
        if (hunters.contains(event.getWhoClicked().getName()))
        {
            if (!event.getInventory().getType().equals(InventoryType.CREATIVE) && !event.getInventory().getType().equals(InventoryType.CRAFTING))
            {
                if (event.getOldCursor().getType().equals(Material.COMPASS) && event.getOldCursor().containsEnchantment(Enchantment.DURABILITY))
                {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void changeWorld(PlayerChangedWorldEvent event)
    {
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 4));
    }
}
