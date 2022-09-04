--
-- Name: countries; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.countries
(
    id       bigint NOT NULL,
    iso_code character varying(3),
    name     character varying(100)
);


ALTER TABLE public.countries OWNER TO postgres;

--
-- Name: countries_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.countries_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


ALTER TABLE public.countries_id_seq OWNER TO postgres;

--
-- Name: countries_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.countries_id_seq OWNED BY public.countries.id;



--
-- Name: currencies; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.currencies
(
    currency_id            bigint                 NOT NULL,
    currency_code          character varying(255),
    currency_currency_code character varying(255) NOT NULL,
    currency_name          character varying(255),
    currency_supported     boolean
);


ALTER TABLE public.currencies OWNER TO postgres;

--
-- Name: currencies_currency_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.currencies_currency_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


ALTER TABLE public.currencies_currency_id_seq OWNER TO postgres;

--
-- Name: currencies_currency_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.currencies_currency_id_seq OWNED BY public.currencies.currency_id;


--
-- Name: languages; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.languages
(
    id            bigint                 NOT NULL,
    date_created  timestamp without time zone,
    date_modified timestamp without time zone,
    deleted       character varying(1) DEFAULT 0,
    code          character varying(255) NOT NULL,
    sort_order    integer,
    title         character varying(255) NOT NULL
);


ALTER TABLE public.languages OWNER TO postgres;

--
-- Name: languages_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.languages_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


ALTER TABLE public.languages_id_seq OWNER TO postgres;

--
-- Name: languages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.languages_id_seq OWNED BY public.languages.id;




--
-- Name: countries id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.countries ALTER COLUMN id SET DEFAULT nextval('public.countries_id_seq'::regclass);


--
-- Name: currencies currency_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.currencies ALTER COLUMN currency_id SET DEFAULT nextval('public.currencies_currency_id_seq'::regclass);

--
-- Name: languages id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.languages ALTER COLUMN id SET DEFAULT nextval('public.languages_id_seq'::regclass);


--
-- Name: countries countries_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.countries
    ADD CONSTRAINT countries_pkey PRIMARY KEY (id);


--
-- Name: currencies currencies_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.currencies
    ADD CONSTRAINT currencies_pkey PRIMARY KEY (currency_id);

--
-- Name: languages languages_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.languages
    ADD CONSTRAINT languages_pkey PRIMARY KEY (id);



--
-- Name: currencies uk_8mramfdinct40l3hsd15fob3i; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.currencies
    ADD CONSTRAINT uk_8mramfdinct40l3hsd15fob3i UNIQUE (currency_name);


--
-- Name: currencies uk_jekn45c17p62ja9i4g7xj1st8; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.currencies
    ADD CONSTRAINT uk_jekn45c17p62ja9i4g7xj1st8 UNIQUE (currency_code);


--
-- Name: currencies uk_o10hj594ximk9mmmwbnnajwsp; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.currencies
    ADD CONSTRAINT uk_o10hj594ximk9mmmwbnnajwsp UNIQUE (currency_currency_code);



--
-- Name: code_idx2; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX code_idx2 ON public.languages USING btree (code);
