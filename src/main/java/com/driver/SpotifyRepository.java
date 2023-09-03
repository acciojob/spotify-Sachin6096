package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
       User u = new User(name,mobile);
       users.add(u);
       return u;
    }

    public Artist createArtist(String name) {
        Artist a = new Artist(name);
        artists.add(a);
        return a;
    }

    public Album createAlbum(String title, String artistName) {

        for(Artist A : artists)
        {
            if(A.getName() == artistName)
            {
                Album a = new Album(title);
                albums.add(a);
                List<Album> list = new ArrayList<>();
                list.add(a);
                artistAlbumMap.put(A,list);
                return a;
            }
        }
        Artist A = new Artist(artistName);
        Album a = new Album(title);
        albums.add(a);
        artistAlbumMap.put(A,albums);
        return a;

    }

    public Song createSong(String title, String albumName, int length) throws Exception{

        for(Album a : albums)
        {
            if(a.getTitle() == albumName)
            {
                Song s = new Song(title,length);
                songs.add(s);
                if(albumSongMap.containsKey(a))
                {
                    List<Song> l = albumSongMap.get(a);
                    l.add(s);
                    albumSongMap.put(a,l);
                }
                else {
                    List<Song> l = new ArrayList<>();
                    l.add(s);
                    albumSongMap.put(a,l);
                }
                return s;
            }
        }

        throw new Exception("Album does not exist");
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        for(User u : users)
        {
            if(u.getMobile() == mobile)
            {
                Playlist p = new Playlist(title);
                playlists.add(p);
                List<Song> list = new ArrayList<>();
                for(Song s : songs)
                {
                    if(s.getLength() == length)
                    {
                        list.add(s);
                    }
                }
                playlistSongMap.put(p,list);

                List<User> user1 = new ArrayList<>();
                user1.add(u);
                playlistListenerMap.put(p,user1);
                creatorPlaylistMap.put(u,p);

                if(userPlaylistMap.containsKey(u))
                {
                    List<Playlist> pl = userPlaylistMap.get(u);
                    pl.add(p);
                    userPlaylistMap.put(u,pl);
                }
                else {
                    List<Playlist> pl = new ArrayList<>();
                    pl.add(p);
                    userPlaylistMap.put(u,pl);
                }
                return p;
            }
        }

        throw new Exception("User does not exist");
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {

       User user = null;
       for(User user1: users)
       {
           if (user1.getMobile() == mobile)
           {
               user = user1;
               break;
           }
       }

       if(user == null)
       {
           throw new Exception("User does not exist");
       }

       Playlist playlist = new Playlist(title);
       playlists.add(playlist);

       List<Song> list = new ArrayList<>();
       for(Song s : songs)
       {
           if(songTitles.contains(s.getTitle())) list.add(s);
       }
       playlistSongMap.put(playlist,list);

       List<User> list1 = new ArrayList<>();
       list1.add(user);
       playlistListenerMap.put(playlist,list1);
       creatorPlaylistMap.put(user,playlist);

       if(userPlaylistMap.containsKey(user))
       {
           List<Playlist> userplaylist = userPlaylistMap.get(user);
           userplaylist.add(playlist);

           userPlaylistMap.put(user,userplaylist);
       }
       else {
           List<Playlist> userplaylist = new ArrayList<>();
           userplaylist.add(playlist);
           userPlaylistMap.put(user,userplaylist);
       }
       return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {

        User user = null;
        for(User u : users)
        {
            if(u.getMobile() == mobile)
            {
                user = u;
                break;
            }
        }

        if(user == null)
        {
            throw new Exception("User does not exist");
        }

        Playlist p = null;
        for(Playlist pl : playlists)
        {
            if(pl.getTitle() == playlistTitle)
            {
                p = pl;
                break;
            }
        }
        if(p == null)
        {
            throw new Exception("Playlist does not exist");
        }

        if(creatorPlaylistMap.containsKey(user)) return p;

        List<User> listner = playlistListenerMap.get(p);
        for(User user1 : listner)
        {
            if(user1 == user)
            {
                return p;
            }
        }

        listner.add(user);
        playlistListenerMap.put(p,listner);

        List<Playlist> pl1 = userPlaylistMap.get(user);
        if(pl1 == null) pl1 = new ArrayList<>();
        pl1.add(p);
        userPlaylistMap.put(user,pl1);
        return p;

    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user = null;
        for(User user1:users){
            if(user1.getMobile()==mobile){
                user=user1;
                break;
            }
        }
        if(user==null)
            throw new Exception("User does not exist");

        Song song = null;
        for(Song song1:songs){
            if(song1.getTitle()==songTitle){
                song=song1;
                break;
            }
        }
        if (song==null)
            throw new Exception("Song does not exist");

        if(songLikeMap.containsKey(song)){
            List<User> list = songLikeMap.get(song);
            if(list.contains(user)){
                return song;
            }else {
                int likes = song.getLikes() + 1;
                song.setLikes(likes);
                list.add(user);
                songLikeMap.put(song,list);

                Album album=null;
                for(Album album1:albumSongMap.keySet()){
                    List<Song> songList = albumSongMap.get(album1);
                    if(songList.contains(song)){
                        album = album1;
                        break;
                    }
                }
                Artist artist = null;
                for(Artist artist1:artistAlbumMap.keySet()){
                    List<Album> albumList = artistAlbumMap.get(artist1);
                    if (albumList.contains(album)){
                        artist = artist1;
                        break;
                    }
                }
                int likes1 = artist.getLikes() +1;
                artist.setLikes(likes1);
                artists.add(artist);
                return song;
            }
        }else {
            int likes = song.getLikes() + 1;
            song.setLikes(likes);
            List<User> list = new ArrayList<>();
            list.add(user);
            songLikeMap.put(song,list);

            Album album=null;
            for(Album album1:albumSongMap.keySet()){
                List<Song> songList = albumSongMap.get(album1);
                if(songList.contains(song)){
                    album = album1;
                    break;
                }
            }
            Artist artist = null;
            for(Artist artist1:artistAlbumMap.keySet()){
                List<Album> albumList = artistAlbumMap.get(artist1);
                if (albumList.contains(album)){
                    artist = artist1;
                    break;
                }
            }
            int likes1 = artist.getLikes() +1;
            artist.setLikes(likes1);
            artists.add(artist);

            return song;
        }
    }

    public String mostPopularArtist() {
        int max = 0;
        Artist artist1=null;

        for(Artist artist:artists){
            if(artist.getLikes()>=max){
                artist1=artist;
                max = artist.getLikes();
            }
        }
        if(artist1==null)
            return null;
        else
            return artist1.getName();
    }

    public String mostPopularSong() {
        int max = 0;
        Song song = null;

        for (Song song1 : songLikeMap.keySet()) {
            if (song1.getLikes() >= max) {
                song = song1;
                max = song1.getLikes();
            }
        }
        if (song == null)
            return null;
        else
            return song.getTitle();
    }
}
