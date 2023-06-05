PGDMP          5                {           evcharge    15.3    15.3                0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false                       0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false                       0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false                       1262    16419    evcharge    DATABASE     �   CREATE DATABASE evcharge WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'English_United States.1252';
    DROP DATABASE evcharge;
                postgres    false            �            1259    16441    stations    TABLE     �   CREATE TABLE public.stations (
    id integer NOT NULL,
    name character varying(255),
    plug_ids character varying(255)
);
    DROP TABLE public.stations;
       public         heap    postgres    false            �            1259    16425    stations_seq    SEQUENCE     v   CREATE SEQUENCE public.stations_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.stations_seq;
       public          postgres    false            �            1259    16446    vehicles    TABLE       CREATE TABLE public.vehicles (
    id integer NOT NULL,
    soccurrent integer,
    chr_dis_per_hour integer,
    constraints_penalty character varying(255),
    favourite_time_slots character varying(255),
    max_capacity integer,
    minsoc integer,
    csid integer
);
    DROP TABLE public.vehicles;
       public         heap    postgres    false            �            1259    16431    vehicles_seq    SEQUENCE     v   CREATE SEQUENCE public.vehicles_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.vehicles_seq;
       public          postgres    false            �          0    16441    stations 
   TABLE DATA           6   COPY public.stations (id, name, plug_ids) FROM stdin;
    public          postgres    false    216   �                  0    16446    vehicles 
   TABLE DATA           �   COPY public.vehicles (id, soccurrent, chr_dis_per_hour, constraints_penalty, favourite_time_slots, max_capacity, minsoc, csid) FROM stdin;
    public          postgres    false    217   E                  0    0    stations_seq    SEQUENCE SET     ;   SELECT pg_catalog.setval('public.stations_seq', 1, false);
          public          postgres    false    214                       0    0    vehicles_seq    SEQUENCE SET     ;   SELECT pg_catalog.setval('public.vehicles_seq', 1, false);
          public          postgres    false    215            k           2606    16452    stations stations_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.stations
    ADD CONSTRAINT stations_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.stations DROP CONSTRAINT stations_pkey;
       public            postgres    false    216            m           2606    16454    vehicles vehicles_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.vehicles
    ADD CONSTRAINT vehicles_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.vehicles DROP CONSTRAINT vehicles_pkey;
       public            postgres    false    217            n           2606    16455 $   vehicles fk7k9q1et0hkqiynp5bdf9buyr9    FK CONSTRAINT     �   ALTER TABLE ONLY public.vehicles
    ADD CONSTRAINT fk7k9q1et0hkqiynp5bdf9buyr9 FOREIGN KEY (csid) REFERENCES public.stations(id);
 N   ALTER TABLE ONLY public.vehicles DROP CONSTRAINT fk7k9q1et0hkqiynp5bdf9buyr9;
       public          postgres    false    216    3179    217            �   x   x�5̱�0��n�?����\"�C4Gr�%'�,��X%�������$�E5�9c�h;�;ρNEU��"�e���@�E��[ї��
�Ǡ���A��1Q�Zn��y{ϫ�HԹ�w��o~"~            x��V[�!����M+�=w���c�> wL&�I�EQ@#��}r�G�|����p8
..���1Ĩ�}�gq�t�#�����>\��Ď�k��5�=��%�R�*z!-A��c�����{��H7$1:u:F��AYM�F��IU���d�����z����/��ܲ)V�>/I�	�ց������d��#0�+yd�OmP7Gy��T�X˱����!H���h���!��$X�y�L3F�ƅ�\Ҳ�	\���)ِ�.�N�A�LHu�6O�l�*~�	����bI._J�{kΛFf��a�=f� ���4A25q��*���Q#�ە'J��oB�꽙���iy�@y����6�4�Ʈ���
.�j?^z{7�3gd{�y�&y�Ԛ�3uҷ�l,԰yy�kSV��	�[`a�~�����j������ͣ�w�&)�ԫ��g�2���y_��M��X,������hfu4&�L��ŧ���!��*O2�ѽ�.�RV �� ����     