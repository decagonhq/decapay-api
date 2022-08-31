--
-- PostgreSQL database dump
--

-- Dumped from database version 12.3
-- Dumped by pg_dump version 12.3

-- Started on 2022-08-28 15:53:22 WAT

SET
statement_timeout = 0;
SET
lock_timeout = 0;
SET
idle_in_transaction_session_timeout = 0;
SET
client_encoding = 'UTF8';
SET
standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET
check_function_bodies = false;
SET
xmloption = content;
SET
client_min_messages = warning;
SET
row_security = off;

SET
default_tablespace = '';

SET
default_table_access_method = heap;

--
-- TOC entry 203 (class 1259 OID 26357)
-- Name: auth; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.auth
(
    id         bigint NOT NULL,
    device_id  character varying(255),
    time_stamp timestamp without time zone,
    token      character varying(255),
    user_id    bigint
);


ALTER TABLE public.auth OWNER TO postgres;

--
-- TOC entry 202 (class 1259 OID 26355)
-- Name: auth_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.auth_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


ALTER TABLE public.auth_id_seq OWNER TO postgres;

--
-- TOC entry 3225 (class 0 OID 0)
-- Dependencies: 202
-- Name: auth_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.auth_id_seq OWNED BY public.auth.id;


--
-- TOC entry 205 (class 1259 OID 26368)
-- Name: budget_categories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.budget_categories
(
    id            bigint NOT NULL,
    date_created  timestamp without time zone,
    date_modified timestamp without time zone,
    deleted       character varying(1) DEFAULT 0,
    title         character varying(100),
    user_id       bigint
);


ALTER TABLE public.budget_categories OWNER TO postgres;

--
-- TOC entry 204 (class 1259 OID 26366)
-- Name: budget_categories_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.budget_categories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


ALTER TABLE public.budget_categories_id_seq OWNER TO postgres;

--
-- TOC entry 3226 (class 0 OID 0)
-- Dependencies: 204
-- Name: budget_categories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.budget_categories_id_seq OWNED BY public.budget_categories.id;


--
-- TOC entry 206 (class 1259 OID 26375)
-- Name: budget_line_items; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.budget_line_items
(
    date_created              timestamp without time zone,
    date_modified             timestamp without time zone,
    deleted                   character varying(1) DEFAULT 0,
    notification_threshold    character varying(100),
    projected_amount          numeric(10, 2)       DEFAULT 0,
    total_amount_spent_so_far numeric(10, 2)       DEFAULT 0,
    budget_category_id        bigint NOT NULL,
    budget_id                 bigint NOT NULL
);


ALTER TABLE public.budget_line_items OWNER TO postgres;

--
-- TOC entry 208 (class 1259 OID 26385)
-- Name: budgets; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.budgets
(
    id                        bigint                 NOT NULL,
    date_created              timestamp without time zone,
    date_modified             timestamp without time zone,
    deleted                   character varying(1) DEFAULT 0,
    budget_end_date           date,
    budget_period             character varying(255) NOT NULL,
    budget_start_date         date,
    description               character varying(255),
    notification_threshold    character varying(100),
    projected_amount          numeric(10, 2)       DEFAULT 0,
    title                     character varying(100),
    total_amount_spent_so_far numeric(10, 2)       DEFAULT 0,
    parent_budget_id          bigint,
    user_id                   bigint                 NOT NULL
);


ALTER TABLE public.budgets OWNER TO postgres;

--
-- TOC entry 207 (class 1259 OID 26383)
-- Name: budgets_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.budgets_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


ALTER TABLE public.budgets_id_seq OWNER TO postgres;

--
-- TOC entry 3227 (class 0 OID 0)
-- Dependencies: 207
-- Name: budgets_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.budgets_id_seq OWNED BY public.budgets.id;


--
-- TOC entry 210 (class 1259 OID 26399)
-- Name: expenses; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.expenses
(
    id                  bigint NOT NULL,
    amount              numeric(10, 2)       DEFAULT 0,
    date_created        timestamp without time zone,
    date_modified       timestamp without time zone,
    deleted             character varying(1) DEFAULT 0,
    description         character varying(255),
    transaction_date    date,
    budget_category_id  bigint,
    budget_id           bigint,
    budget_line_item_id bigint
);


ALTER TABLE public.expenses OWNER TO postgres;

--
-- TOC entry 209 (class 1259 OID 26397)
-- Name: expenses_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.expenses_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


ALTER TABLE public.expenses_id_seq OWNER TO postgres;

--
-- TOC entry 3228 (class 0 OID 0)
-- Dependencies: 209
-- Name: expenses_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.expenses_id_seq OWNED BY public.expenses.id;


--
-- TOC entry 212 (class 1259 OID 26409)
-- Name: password_resets; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.password_resets
(
    id            bigint NOT NULL,
    date_created  timestamp without time zone,
    date_modified timestamp without time zone,
    deleted       character varying(1) DEFAULT 0,
    device_id     character varying(30),
    email         character varying(255),
    expired_at    timestamp without time zone,
    status        character varying(255),
    token         character varying(255)
);


ALTER TABLE public.password_resets OWNER TO postgres;

--
-- TOC entry 211 (class 1259 OID 26407)
-- Name: password_resets_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.password_resets_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


ALTER TABLE public.password_resets_id_seq OWNER TO postgres;

--
-- TOC entry 3229 (class 0 OID 0)
-- Dependencies: 211
-- Name: password_resets_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.password_resets_id_seq OWNED BY public.password_resets.id;


--
-- TOC entry 214 (class 1259 OID 26421)
-- Name: token_blacklist; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.token_blacklist
(
    id    integer NOT NULL,
    token character varying(255)
);


ALTER TABLE public.token_blacklist OWNER TO postgres;

--
-- TOC entry 213 (class 1259 OID 26419)
-- Name: token_blacklist_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.token_blacklist_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


ALTER TABLE public.token_blacklist_id_seq OWNER TO postgres;

--
-- TOC entry 3230 (class 0 OID 0)
-- Dependencies: 213
-- Name: token_blacklist_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.token_blacklist_id_seq OWNED BY public.token_blacklist.id;


--
-- TOC entry 216 (class 1259 OID 26429)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users
(
    id            bigint NOT NULL,
    date_created  timestamp without time zone,
    date_modified timestamp without time zone,
    deleted       character varying(1) DEFAULT 0,
    email         character varying(100),
    first_name    character varying(100),
    last_login    timestamp without time zone,
    last_name     character varying(100),
    password      character varying(64),
    phone_number  character varying(50),
    user_status   character varying(255)
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 215 (class 1259 OID 26427)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO postgres;

--
-- TOC entry 3231 (class 0 OID 0)
-- Dependencies: 215
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- TOC entry 3048 (class 2604 OID 26360)
-- Name: auth id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.auth ALTER COLUMN id SET DEFAULT nextval('public.auth_id_seq'::regclass);


--
-- TOC entry 3049 (class 2604 OID 26371)
-- Name: budget_categories id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.budget_categories ALTER COLUMN id SET DEFAULT nextval('public.budget_categories_id_seq'::regclass);


--
-- TOC entry 3054 (class 2604 OID 26388)
-- Name: budgets id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.budgets ALTER COLUMN id SET DEFAULT nextval('public.budgets_id_seq'::regclass);


--
-- TOC entry 3058 (class 2604 OID 26402)
-- Name: expenses id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.expenses ALTER COLUMN id SET DEFAULT nextval('public.expenses_id_seq'::regclass);


--
-- TOC entry 3061 (class 2604 OID 26412)
-- Name: password_resets id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.password_resets ALTER COLUMN id SET DEFAULT nextval('public.password_resets_id_seq'::regclass);


--
-- TOC entry 3063 (class 2604 OID 26424)
-- Name: token_blacklist id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token_blacklist ALTER COLUMN id SET DEFAULT nextval('public.token_blacklist_id_seq'::regclass);


--
-- TOC entry 3064 (class 2604 OID 26432)
-- Name: users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- TOC entry 3067 (class 2606 OID 26365)
-- Name: auth auth_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.auth
    ADD CONSTRAINT auth_pkey PRIMARY KEY (id);


--
-- TOC entry 3071 (class 2606 OID 26374)
-- Name: budget_categories budget_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.budget_categories
    ADD CONSTRAINT budget_categories_pkey PRIMARY KEY (id);


--
-- TOC entry 3073 (class 2606 OID 26382)
-- Name: budget_line_items budget_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.budget_line_items
    ADD CONSTRAINT budget_line_items_pkey PRIMARY KEY (budget_category_id, budget_id);


--
-- TOC entry 3075 (class 2606 OID 26396)
-- Name: budgets budgets_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.budgets
    ADD CONSTRAINT budgets_pkey PRIMARY KEY (id);


--
-- TOC entry 3077 (class 2606 OID 26406)
-- Name: expenses expenses_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.expenses
    ADD CONSTRAINT expenses_pkey PRIMARY KEY (id);


--
-- TOC entry 3079 (class 2606 OID 26418)
-- Name: password_resets password_resets_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.password_resets
    ADD CONSTRAINT password_resets_pkey PRIMARY KEY (id);


--
-- TOC entry 3083 (class 2606 OID 26426)
-- Name: token_blacklist token_blacklist_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token_blacklist
    ADD CONSTRAINT token_blacklist_pkey PRIMARY KEY (id);


--
-- TOC entry 3069 (class 2606 OID 26440)
-- Name: auth uk3qatnns5xiv0d2hctdv0i1wga; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.auth
    ADD CONSTRAINT uk3qatnns5xiv0d2hctdv0i1wga UNIQUE (user_id, device_id);


--
-- TOC entry 3085 (class 2606 OID 26444)
-- Name: users uk_6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- TOC entry 3081 (class 2606 OID 26442)
-- Name: password_resets ukcqdkk6ccrmqwiip28js73qjjk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.password_resets
    ADD CONSTRAINT ukcqdkk6ccrmqwiip28js73qjjk UNIQUE (email, device_id);


--
-- TOC entry 3087 (class 2606 OID 26438)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 3091 (class 2606 OID 26460)
-- Name: budgets fk7ov4n4g0qhoyqneajkec672vw; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.budgets
    ADD CONSTRAINT fk7ov4n4g0qhoyqneajkec672vw FOREIGN KEY (parent_budget_id) REFERENCES public.budgets(id);


--
-- TOC entry 3090 (class 2606 OID 26455)
-- Name: budget_line_items fk9jewgdv8byqarc1cc3v95b1x6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.budget_line_items
    ADD CONSTRAINT fk9jewgdv8byqarc1cc3v95b1x6 FOREIGN KEY (budget_category_id) REFERENCES public.budget_categories(id);


--
-- TOC entry 3092 (class 2606 OID 26465)
-- Name: budgets fkln0tm5tgf3f9q3sp9sa5m8m7b; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.budgets
    ADD CONSTRAINT fkln0tm5tgf3f9q3sp9sa5m8m7b FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- TOC entry 3088 (class 2606 OID 26445)
-- Name: budget_categories fkm5qlang2cio94kb3heobpbydc; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.budget_categories
    ADD CONSTRAINT fkm5qlang2cio94kb3heobpbydc FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- TOC entry 3089 (class 2606 OID 26450)
-- Name: budget_line_items fkrlnmgurqiy7kjmrwhxwaooyed; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.budget_line_items
    ADD CONSTRAINT fkrlnmgurqiy7kjmrwhxwaooyed FOREIGN KEY (budget_id) REFERENCES public.budgets(id);


--
-- TOC entry 3093 (class 2606 OID 26470)
-- Name: expenses fks1uvda54hlr2t42s1wwjvcbwr; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.expenses
    ADD CONSTRAINT fks1uvda54hlr2t42s1wwjvcbwr FOREIGN KEY (budget_category_id, budget_id) REFERENCES public.budget_line_items(budget_category_id, budget_id);


-- Completed on 2022-08-28 15:53:22 WAT

--
-- PostgreSQL database dump complete
--

