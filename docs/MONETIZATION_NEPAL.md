# Earning money on Google Play as a Nepal-registered developer
**Status:** reference document · **Last verified against Google policy pages:** 2026-09-30
**Scope:** Play Console account capability, merchant-registration limits, and every
policy-sanctioned monetization route available to a developer whose account country
is Nepal.

> **How to read this document.** Every claim is tagged:
> `[VERIFIED]` = quoted or paraphrased directly from a linked Google policy page on
> the verification date.
> `[INFERENCE]` = my reasoning from verified facts, not stated by Google.
> `[VERIFY]` = plausible but you must confirm with Google or a professional before acting.
>
> Policy pages change. The links are the authority; if this doc and a linked page
> disagree, the page wins. Re-verify before any irreversible action (registering an
> entity, signing a lease).

---

## 1. Executive summary

| # | Question | Answer |
|---|----------|--------|
| 1 | Can a Nepal-registered dev publish a free app on Play? | **Yes** |
| 2 | Can a Nepal-registered dev run AdMob and get paid in Nepal? | **Yes** — Nepal is an AdMob-supported country |
| 3 | Can they sell a paid app, or in-app purchases, via Play? | **No** — merchant registration is ✘ for Nepal |
| 4 | Can they fix merchant registration later? | **No** — the country is locked at profile creation |
| 5 | Can they change their *developer account* country? | **No** — new account required, then transfer apps |
| 6 | Best zero-cost path to revenue | **AdMob** (ads), Nepal wire transfer payout |
| 7 | Best path to selling digital content | **Consumption-only app + web checkout**, or a real entity in a supported country |
| 8 | What must never be done | Fake addresses, nominee shells, bought/sold accounts |

**The core insight most developers miss:** *merchant registration and Play developer
registration are two different layers.* Losing the first does **not** cost you the
second. You can publish, distribute, and earn ad revenue today with a Nepal account.
What you lose is only the *ability to collect money from users through Play*.

---

## 2. The two layers, explained

Most confusion here comes from conflating these.

```
Layer 1 — Play Console Developer Account
  Created at play.google.com/apps/publish ($25 one-time)
  Requires: developer registration support in your country
  Nepal: ✔ SUPPORTED
  Grants: create apps, upload builds, publish, stats, reviews
  Does NOT require a merchant profile.

Layer 2 — Google Payments Profile (merchant registration)
  Created in Google Payments Center, auto-links to Play Console
  Requires: merchant registration support in your country
            + physical business address + bank account in the SAME country
  Nepal: ✘ NOT SUPPORTED
  Grants: Play Billing, paid apps, subscriptions, in-app products, sales payouts
```

`[VERIFIED]` From
[Supported locations for developer and merchant registration](https://support.google.com/googleplay/android-developer/answer/9306917):

| Location | Developer registration | Merchant registration | Default currency |
|---|---|---|---|
| India | ✔ | ✔ | INR |
| Nepal | ✔ | ✘ | — |

`[VERIFIED]` From
[Create a payments profile](https://support.google.com/googleplay/android-developer/answer/7161426):

> "Later, you'll need to make sure that your bank account is registered in the same
> country listed in your payments profile."

`[VERIFIED]` Same page:

> "You can't change your business location country but you can change your public
> merchant and payments profiles later."

**Consequence:** the country is permanent for that profile. Even a public-information
refresh will not unlock Nepal. `[VERIFIED]`

---

## 3. What exactly the ✘ blocks, and what it does not

### 3.1 Blocked — requires merchant registration

| Capability | Why |
|---|---|
| Sell a **paid app** (download fee) | Payments policy §1: "Developers charging for app downloads from Google Play must use Google Play's billing system" |
| **In-app products** (packs, coins, Pro unlock, one-time purchases) | §2 requires Play Billing |
| **Subscriptions** | §2, explicitly listed |
| Collect **any** revenue through Play | No merchant profile → no payout destination |
| Participate in **alternative billing programs** | Each program requires "set up a payment profile as required" |

`[VERIFIED]` From
[Changes to billing requirements for developers serving users in India](https://support.google.com/googleplay/android-developer/answer/13306652)
— step 2 of enrollment explicitly requires setting up a payment profile. This is
stated for India, and every other alternative-billing program carries the same
requirement. `[INFERENCE]`

### 3.2 NOT blocked — fully available to you today

| Capability | Notes |
|---|---|
| Publish unlimited free apps | Nepal supports developer registration |
| **AdMob ads and payouts** | Nepal is a supported AdMob country (see §4) |
| Store listings, screenshots, store optimisation | Unaffected |
| Firebase (free tier) | Independent of Play billing |
| Supabase / any backend | Unaffected |
| Release management, staged rollout, A/B tests | Unaffected |
| Google Play Developer Program Policies apply in full | Merchant status grants no exemption from policy |

**The practical upshot:** your only real loss is *user-paid* revenue. Ad revenue —
which for a Nepal-traffic quiz app is your dominant realistic source anyway — is
unaffected.

---

## 4. Path A — AdMob (the recommended primary route)

### 4.1 Nepal is supported

`[VERIFIED]` From [AdMob availability](https://support.google.com/admob/answer/16451422),
the Asia Pacific list includes: Bangladesh, Bhutan, Cambodia, India, Indonesia,
Malaysia, **Nepal**, Pakistan, Philippines, Singapore, Sri Lanka, Thailand, Viet Nam.

**Critically: AdMob does not require a Play merchant profile.** It runs on the
AdSense payments system, which is a separate registration. `[VERIFIED]`
See [Add your payment method for AdSense](https://support.google.com/admob/answer/1714397).

You can sign up for AdMob and get paid with only a Nepal developer account.

### 4.2 Payout mechanics in Nepal — read this carefully

`[VERIFIED]` From [Add your payment method for AdSense or AdSense for YouTube](https://support.google.com/admob/answer/1714397),
APAC table, row **Nepal**:

| AdSense — Check | EFT | Wire | Hyperwallet |
|---|---|---|---|
| Yes | **No** | Yes | **No** |

**What this means practically:**
- ✅ **Wire transfer (SWIFT)** — your primary option. Your Nepali bank needs a
  SWIFT/USD-receiving capability. Virtually all commercial Nepali banks (Nabil,
  NIC Asia, Everest, SBI, Himal, etc.) have this. Verify with your bank.
- ✅ **Check** — available, but you are outside the US and delivery is slow/expensive.
  Use only as fallback.
- ❌ **No EFT / local clearing** — no same-day local transfer. Every payout crosses
  borders and incurs intermediary-bank fees.
- ❌ **No PayPal Hyperwallet.**

`[INFERENCE]` **Cost leakage is the hidden trap.** On a $200 wire, intermediary and
receiving-bank fees of $15–35 are plausible — that is 7–17% of revenue gone. Verify
the exact fee schedule with your bank *before* you reach the payout threshold, and
ask specifically about SWIFT incoming fees and whether they are shared (OUR/SHA/BEN).
`[VERIFY]`

`[VERIFIED]` Payout threshold is USD 100 (standard AdSense/AdMob threshold; confirm
current value on the Payments page).

### 4.3 AdMob policy constraints relevant to a quiz app

`[VERIFIED]` From [Ads policy](https://support.google.com/googleplay/android-developer/answer/9857753)
and [Real-Money Gambling, Games, and Contests](https://support.google.com/googleplay/android-developer/answer/9877032):

1. **Deceptive ads.** Ads must not appear as buttons, icons, or interactive UI. In
   a quiz your answer options *are* buttons — an ad block placed adjacent to them
   is the single most common rejection reason for quiz apps. Ads must be visually
   separated and labelled.
2. **No ads interrupting gameplay** in a way that causes accidental interaction.
   Your own roadmap §8 already forbids this; keep that rule.
3. **No ads on app open/splash**, and none overlapping system UI or close controls.
4. **Rewarded ads** must be opt-in, must disclose the reward before the ad starts,
   and must not grant the reward for an abandoned ad.
5. **No ad network may be added** that violates Play policies — this is your
   responsibility even for third-party SDKs. `[VERIFIED]` Play Console Requirements §3
   tail: "make sure that your app provides... everything in your app, including ad
   networks, analytics services, and third-party SDKs, complies."
6. **Data Safety** declaration must include ad-related data collection (advertising
   ID, etc.).
7. If you later register under the **Designed for Families** programme, personalised
   ads are restricted. `[VERIFIED]` Real-Money policy requirement 4 (for gambling ads)
   and the Families policy interact here.

### 4.4 Realistic Nepal revenue math

`[VERIFY]` — the $0.31 CPM figure is from third-party aggregator benchmarks
(SR Zone eCPM tables), not from Google. Google does not publish Nepal CPMs. Treat
these as directional and validate with your own AdMob dashboard once you have data.

| Format | Nepal CPM (est.) | Impressions/DAU/month (30 sessions × 6 imp.) | Revenue per DAU/month |
|---|---|---|---|
| Banner | $0.20–0.35 | 180 | **$0.04–0.06** |
| Interstitial (Result screen only) | $0.30–0.45 | 90 | **$0.03–0.04** |
| Rewarded video | $1.50–4.00 | 45 | **$0.07–0.18** |

`[INFERENCE]` Realistic blended ARPU/DAU in Nepal: **$0.10–0.25/month**.

| Daily actives | Monthly revenue (est.) |
|---|---|
| 1,000 | $100–250 |
| 10,000 | $1,000–2,500 |
| 50,000 | $5,000–12,500 |
| 100,000 | $10,000–25,000 |

**The strategic implication:** Nepal-only ads will not make you rich. Your real
levers are (a) rewarded video volume, (b) growing the Nepali diaspora in **India**
and the Gulf where CPMs are 3–10× higher, and (c) building a product worth buying.

---

## 5. Path B — Getting merchant registration legitimately

There is **no way to change the country** of an existing payments profile
`[VERIFIED]`, and **no way to change your Play developer account country** — per
Google's own community answer: *"You can't change countries. You will need to create
a new account and transfer the apps."* `[VERIFIED]` (Google Platinum Product
Expert, [Play Console Community thread](https://support.google.com/googleplay/android-developer/thread/312601124/changing-countries-as-a-google-play-developer))

So there are only two routes.

### 5.1 Route B1 — Register a **genuine** legal entity in a supported country

This works. It is also expensive and creates real obligations. What you need for an
Organization account in, say, India: `[VERIFIED]` for the requirements, `[INFERENCE]`
for the specifics.

| Requirement | Source | Detail |
|---|---|---|
| Legal entity registered in that country | Play Console Requirements §2.2 | Name must match your D-U-N-S profile |
| D-U-N-S number | Play Console Requirements §2.1.2 | "can take 30 days or more to obtain one" `[VERIFIED]` |
| Organisation registration document | Play Console Requirements | Must be current, issued by a trustworthy authority |
| Physical business address, no PO box | Create a payments profile `[VERIFIED]` | |
| Business bank account in that country | Create a payments profile `[VERIFIED]` | |
| VAT/tax ID in that country's format | Resolve setup issues `[VERIFIED]` | |
| Public business website + support email | Create a payments profile `[VERIFIED]` | Shown to users |

**Cost reality `[VERIFY]`:** entity registration + CA fees + a registered office +
annual compliance + GST/company tax in that jurisdiction + repatriation of funds
back to Nepal + Indian corporate tax on profit. For an indie app with modest revenue,
this is frequently a net loss unless revenue becomes substantial.

**Honesty requirements — this is the part that ends accounts.** `[VERIFIED]` Play
Console Requirements:

> "Submitting unsupported documents is the primary reason for developer verification
> failures, and providing modified or fake documents can lead to the immediate
> removal of your account and apps."

**Explicitly do not do:** `[INFERENCE]`
- Use a nominee director / sham address. Google now verifies organisation documents
  against D-U-N-S, checks names match, and can request additional proof. Mismatch is
  the #1 cause of org verification failure.
- Buy or lease an existing developer account. `[VERIFIED]` The Account Transfer
  policy prohibits "buying, leasing, and selling accounts obtained through
  illegitimate means on third-party marketplaces." This is now an explicit policy
  violation, not just bad practice.
- Put a Nepali address on a foreign-entity application. Also fails D-U-N-S matching.

**If you do this route:** register the entity properly, keep it real, file taxes in
both jurisdictions, and get a CA in that country plus one in Nepal.

### 5.2 Route B2 — Do nothing, and stay free

Underrated and often correct. A free, ad-supported, well-reviewed app on Play costs
you nothing, needs no entity, no D-U-N-S, no foreign tax exposure, and carries no
enhanced-verification risk. Given the Nepal CPM math in §4.4, the revenue difference
between "free + ads" and "paid packs" is often smaller than the compliance overhead
of the paid model — because a free app reaches every user, while a paid app needs a
payment method most Nepali users do not have in Play.

---

## 6. Path C — Sell outside Play (the consumption-only model)

`[VERIFIED]` From [Understanding Google Play's Payments policy](https://support.google.com/googleplay/android-developer/answer/10281818):

> "Any app can be consumption-only, even if it is part of a paid service. For
> example, a user could log in when the app opens and access content paid for
> somewhere else."

And critically, this also applies to the free-with-ads carve-out:

> "You can communicate this [purchase options] without direct links, including using
> language like: 'You can purchase this book directly on our website' … 'Go to our
> website to upgrade your subscription to Premium'"

### 6.1 What this lets you do

| Allowed on Play | Not allowed on Play |
|---|---|
| App says "Pro packs available at quizesque.com" | A button/link inside the app to that checkout |
| Listing text may mention it | A webview showing a payment page |
| Emails/social can link to your site | Any call-to-action that leads off-app to pay |
| Fully free app + ads | Digital item sold by any in-app mechanism |

`[VERIFIED]` Payments policy §4 lists the prohibitions explicitly, "including but not
limited to" — app listing, in-app promotions, webviews, buttons, links, messaging,
**advertisements**, or other calls to action.

**Note the explicit inclusion of advertisements.** You cannot put an ad creative
that says "buy Pro at our website." `[VERIFIED]`

### 6.2 Regional escape hatches — the current state

`[VERIFIED]` Payments policy §8 and §9 now point to new program pages. As of the
verification date:

| Program | Markets | Service fee | Open to non-resident devs? |
|---|---|---|---|
| [India alternative billing](https://support.google.com/googleplay/android-developer/answer/13306652) | India | Standard fee **−4%** | Yes, but requires a payment profile → **blocked for Nepal** |
| [EEA alternative billing](https://support.google.com/googleplay/android-developer/answer/12348241) | EEA | Fee applies | Yes, but payment profile required |
| [EEA External Offers](https://support.google.com/googleplay/android-developer/answer/14372887) | EEA | Fee applies | Yes, payment profile required |
| [South Korea](https://support.google.com/googleplay/android-developer/answer/11222040) | South Korea | Fee applies | Payment profile required |
| [US external content links](https://support.google.com/googleplay/android-developer/answer/16470497) | **US only** | **10%** first $1M (in-app items, new installs); 10–20% other | `[VERIFIED]` "Developers from other regions can enroll in the program to offer external links to users in the United States and its territories." |

**The US program is the notable one.** `[VERIFIED]` Its FAQ explicitly permits
developers based outside the US to enrol, and it is the broadest carve-out currently
available — you may link US users out to purchase digital items, and even to download
your own APK from another store. As of the July 2026 update, developers must report
transactions and pay the service fee from **1 October 2026**, with a reporting/payment
deadline of **1 December 2026**.

`[VERIFY]` Whether enrollment requires a merchant payments profile is the one
question I could not confirm from the published pages. Ask Google support directly —
this is the highest-value question in this document. If it does not require one, it
is your single best route to user-paid revenue.

**Bottom line for Nepal:** these programs are almost certainly closed to you, because
they all require a payments profile and Nepal does not permit one. `[INFERENCE]`

---

## 7. Path D — Sell physical goods or services (a genuine carve-out)

`[VERIFIED]` Payments policy §3.1 — Play Billing **must not** be used when payment
is primarily for:
- physical goods (groceries, clothing, appliances, electronics)
- physical services (transport, cleaning, airfare, gym memberships, food delivery,
  **live event tickets**)
- a remittance of a credit card or utility bill

This means a Play-distributed app that sells *physical* things can transact without
Play Billing. Practical relevance to a quiz app is limited, but two real
applications:

1. **Sponsored quizzes / branded content.** A bank, telco, or edtech brand pays a
   flat fee for a "Nepal GK Championship." That's a **business-to-business service
   sale**, paid by a company — not an in-app purchase by a consumer. Play Billing is
   not required. `[INFERENCE]`
2. **Paid printed or physical products** — a question-bank book, a classroom
   worksheet pack, printed certificates for a coaching centre. `[INFERENCE]`

`[VERIFY]` B2B sponsorship invoicing from a Nepali entity to a brand is ordinary
invoicing outside Play entirely — it has nothing to do with the app, and is simply
good business development.

---

## 8. Other policy carve-outs worth knowing

`[VERIFIED]` All from [Understanding Google Play's Payments policy](https://support.google.com/googleplay/android-developer/answer/10281818):

| Carve-out | Rule | Verdict for a quiz app |
|---|---|---|
| **Gift cards** | "Do I need to use Google Play's billing system to sell gift cards in my app? **No.**" | Interesting — but tying it to quiz score hits the "awarded by game performance" clause. **Grey, not safe.** |
| **Loyalty / reward points** | Earned or awarded points can be issued in-app without Play Billing, and exchanged for digital goods without Play Billing. **Only selling the points triggers billing.** | Potentially usable for a *non-monetary* points system |
| **1:1 online paid services** | Exempt if between two individuals, non-replayable, no recording. Examples: classes, coaching, advisory. | Not applicable to a quiz |
| **Peer-to-peer tips** | Exempt only if 100% goes to the creator and grants no digital content/access | Not applicable |
| **Tokenized assets / NFTs** | If used to buy digital content in-app, Play Billing applies | Not applicable |
| **Alternative billing countries** | "As long as Google Play's billing system isn't available in a particular country, the requirement doesn't apply in that country" | Nepal *is* a Play billing market, so this does not rescue you `[INFERENCE]` |

**The loyalty-points row deserves care.** `[INFERENCE]` A points system that is
**earned** by playing and **redeemed** for in-app cosmetic or content benefits does
not require Play Billing per the text above. But if those points have **any** cash
value, you re-enter the [Real-Money Gambling, Games, and Contests](https://support.google.com/googleplay/android-developer/answer/9877032)
policy — and that policy's Gamified Loyalty table says **"Game" apps: loyalty
gamification and variable rewards = Not Allowed.**

---

## 9. Payments infrastructure outside Play (for Path C)

`[VERIFY]` — verify each before relying on it. This area changes often.

| Provider | Nepal support | Notes |
|---|---|---|
| **Stripe** | **No** | Nepal is not in Stripe's supported-countries list `[VERIFIED by absence in the published list, 2026-09-30]` |
| **PayPal** | Limited | Receiving is possible; holding/withdrawal for Nepalese accounts is restricted |
| **Merchant-of-Record platforms** (Lemon Squeezy, Paddle, Gumroad) | Varies | These are MoR — they handle merchant-of-record, tax collection, and payouts to you. *This is the category to investigate*, because the MoR is the merchant, not you. `[VERIFY]` each platform's seller-country list |
| **eSewa / Khalti / ConnectIPS (Nepal)** | Yes | Real Nepali payment rails — essential if you ever sell *physical* products to Nepali users |

`[INFERENCE]` If you pursue Path C, a Merchant-of-Record platform is structurally the
right answer: it removes the need for you to be a merchant in a supported country. The
catch is that your Play-distributed app must then be consumption-only, with no
in-app link to the checkout — which typically means very few players convert.

---

## 10. Tax and compliance in Nepal (applies to ALL paths)

`[VERIFIED]` Note the direction of Google's withholding: it applies to **your
location** and **transactions in certain markets** —

| Market | WHT | Trigger |
|---|---|---|
| **Nepal** | **None listed** | Nepal does not appear in Google's WHT schedule |
| India | 0.1% (with PAN) / 5% (without PAN) | Only on India-user purchases |
| Brazil | IRRF + CIDE | Only if you are paid in non-BRL |
| Taiwan | 3% if no Taiwan VAT ID | Only on Taiwan-user purchases |
| Tanzania | 5% | Only on Tanzania-user purchases |
| Vietnam | 5% | Only on Vietnam-user purchases |
| Egypt / Kuwait / Myanmar / Sri Lanka | up to 20% / 5% / 2.5% / 10% | Only on direct carrier billing |

`[INFERENCE]` **You have no Google-side withholding exposure as a Nepali developer.**
That is a genuine advantage, not a problem. What you *do* have is a domestic tax
obligation that Google will not handle for you:

`[VERIFY]` — all Nepali items below need a chartered accountant; I am flagging them,
not advising them.
- Register a business (sole proprietorship or Pvt Ltd) before treating this as a
  business rather than a hobby.
- AdSense/AdMob earnings are **business income** in Nepal under the Income Tax Act.
  You likely need to file returns even if below thresholds.
- Consider **advance tax** instalments if income is regular and material.
- Export of services (earning from foreign clients) may qualify for **VAT
  zero-rating** — this materially changes the effective rate. Confirm the conditions.
- Remittances land in USD/EUR; FX conversion and the bank's spread are costs.
- If you later form a foreign entity (§5.1), you have **two** tax regimes and likely
  a withholding/PE (permanent establishment) problem if you manage it from Nepal.
  Get advice *before* forming the entity, not after.

`[INFERENCE]` Practical threshold for getting an accountant: once earnings are
non-trivial and recurring, the cost of a CA session is small relative to the risk of
filing incorrectly.

---

## 11. Enforcement: what actually happens

`[VERIFIED]` Play enforces by: policy warning, app removal, developer account
termination, and for monetary violations, referral to law enforcement. The
[Real-Money Gambling policy](https://support.google.com/googleplay/android-developer/answer/9877032)
explicitly names promoting real-money contests as a violation of
"User Generated Content" and "Restricted Content" categories.

**Specific to your project — the top-up prize idea is permanently off the table:**
1. Nepal is not in the
   [allowed-jurisdictions list](https://support.google.com/googleplay/android-developer/answer/12256011).
2. Skill-based cash prizes are banned by the "Other Real-Money Games, Contests, and
   Tournament Apps" clause.
3. The Gamified Loyalty exception is explicitly **"Not Allowed"** for Game apps.
4. An approved gambling app must be free to download, must **not** use Play Billing,
   must be AO-rated, must verify age, must geofence to licensed territories, and must
   display responsible-gambling info. None of that is compatible with QUIZesque.

This is not a grey area you can argue through. `[INFERENCE]`

---

## 12. Decision tree

```
Do you want to sell digital content through Play?
│
├─ NO (or: not yet) ──────────────────────────────► SHIP FREE + ADMOB
│                                                     • No entity needed
│                                                     • Nepal wire payout
│                                                     • See §4
│
└─ YES
   │
   ├─ Can you form a REAL entity in a supported country?
   │   │
   │   ├─ Yes, and revenue justifies it ───────────► ROUTE B1
   │   │   • D-U-N-S, bank account, tax in 2 countries
   │   │   • Transfer apps from Nepal account to new org account
   │   │   • Then: Play Billing, subscriptions, paid packs
   │   │
   │   └─ No / too expensive ─────────────────────► ROUTE C (consumption-only)
   │                                               • Free app on Play
   │                                               • Web checkout via MoR platform
   │                                               • NO in-app links
   │                                               • Low conversion — accept it
   │
   ├─ Would you sell PHYSICAL goods/services? ────► ROUTE D
   │   • Sponsorships, printed products
   │   • Play Billing not required
   │
   └─ Just want US revenue from US users? ───────► US External Content Links
       • Enrol even from Nepal (explicitly allowed)
       • 10% fee, report from 1 Oct 2026
       • VERIFY enrollment requirements first
```

---

## 13. Action checklist

**Immediately (no cost, no risk):**
- [ ] Set up an AdMob account — Nepal is supported, no merchant profile needed
- [ ] Add bank wire (SWIFT) receiving details; ask your bank the exact incoming
      wire fee and whether it is OUR/SHA/BEN shared
- [ ] Read the [Ads policy](https://support.google.com/googleplay/android-developer/answer/9857753)
      before writing any ad code
- [ ] Read the [Payments policy](https://support.google.com/googleplay/android-developer/answer/9858738)
      in full — it is short and it governs everything
- [ ] Read your own `PRODUCT_ROADMAP.md` §8 ("What NOT to sell") — it is correct and
      predates this analysis

**Before any monetization work:**
- [ ] Consult a Nepali chartered accountant on ad-revenue income tax and VAT
      zero-rating eligibility
- [ ] Register a business entity (even sole proprietorship) if revenue will be regular
- [ ] Open a Play support ticket and ask the two blocking questions in §14

**Only if you pursue an entity:**
- [ ] Talk to a CA in the target country *and* Nepal about PE/tax residency before
      incorporating
- [ ] Obtain D-U-N-S early (30+ days)
- [ ] Get a registered office with a real physical address and matching D-U-N-S record
- [ ] Do not proceed unless the numbers work: entity cost + annual compliance + tax +
      repatriation vs. expected in-app revenue

**Never:**
- [ ] Fake or nominee addresses, or mismatched D-U-N-S documents
- [ ] Buy, sell, or lease a developer account
- [ ] Ship the mobile top-up prize plan on Play
- [ ] Put an in-app link or button to an external digital-goods checkout

---

## 14. Open questions — ask Google

Submit via Play Console → Get help, or the
[developer support form](https://support.google.com/googleplay/android-developer/contact/general_contact).

1. **Does the US external content links program require a merchant payments profile
   to enroll, or may a Nepal-registered developer with no merchant profile enroll?**
   (Highest value question in this document. If no, it is your best route to paid
   digital content.)
2. **Is there any pending plan to open merchant registration for Nepal?** Useful for
   timing a roadmap decision.
3. **Confirmation:** a Nepal-registered developer with no merchant profile can serve
   AdMob ads in a Play-distributed app and receive payouts to a Nepali bank account
   without a Play merchant profile — is this correct?

---

## 15. Source index

All accessed 2026-09-30.

**Play policies**
- [Supported locations for developer and merchant registration](https://support.google.com/googleplay/android-developer/answer/9306917) — Nepal: dev ✔, merchant ✘
- [Payments policy](https://support.google.com/googleplay/android-developer/answer/9858738) — §§1–9
- [Understanding Google Play's Payments policy](https://support.google.com/googleplay/android-developer/answer/10281818) — consumption-only, gift cards, loyalty points, 1:1 services, gift cards, geography carve-out
- [Create a payments profile](https://support.google.com/googleplay/android-developer/answer/7161426) — country is permanent; bank must match country; no PO box
- [Resolve payments profile setup issues](https://support.google.com/googleplay/android-developer/answer/7160669) — unsupported country, physical address, VAT formats
- [Play Console Requirements](https://support.google.com/googleplay/android-developer/answer/10788890) — verification, D-U-N-S, fake documents, Account Transfer policy
- [Verifying your Play Console developer account](https://support.google.com/googleplay/android-developer/answer/14177239) — org verification, D-U-N-S timing
- [Real-Money Gambling, Games, and Contests](https://support.google.com/googleplay/android-developer/answer/9877032) — cash prizes, Gamified Loyalty table
- [Country/region allowances for gambling apps](https://support.google.com/googleplay/android-developer/answer/12256011) — Nepal absent
- [Ads policy](https://support.google.com/googleplay/android-developer/answer/9857753) — deceptive ads
- [Withholding tax (WHT)](https://support.google.com/googleplay/android-developer/answer/9384608) — Nepal absent from schedule
- [Supported locations for distribution to Play users](https://support.google.com/googleplay/android-developer/answer/10532353)
- [Developer account registration payment methods](https://support.google.com/googleplay/android-developer/answer/9875040) — Visa/Mastercard, no prepaid
- [Changing countries as a Play Developer](https://support.google.com/googleplay/android-developer/thread/312601124/changing-countries-as-a-google-play-developer) — Google community answer

**Regional billing programs**
- [India alternative billing](https://support.google.com/googleplay/android-developer/answer/13306652) — 4% fee reduction; payment profile required
- [EEA alternative billing](https://support.google.com/googleplay/android-developer/answer/12348241)
- [EEA External Offers](https://support.google.com/googleplay/android-developer/answer/14372887)
- [South Korea alternative billing](https://support.google.com/googleplay/android-developer/answer/11222040)
- [US external content links](https://support.google.com/googleplay/android-developer/answer/16470497) — open to non-US devs; 10–20% fees; report from 1 Oct 2026, deadline 1 Dec 2026
- [US court order update](https://support.google.com/googleplay/android-developer/answer/15582165)
- [Offering an alternative billing system for users in the US](https://support.google.com/googleplay/android-developer/answer/16497028)

**AdMob**
- [AdMob availability](https://support.google.com/admob/answer/16451422) — Nepal supported
- [Add your payment method for AdSense](https://support.google.com/admob/answer/1714397) — Nepal: check ✔, EFT ✘, wire ✔, Hyperwallet ✘
- [Understanding AdMob country restrictions](https://support.google.com/admob/answer/6163675)
- [AdMob policy change log](https://support.google.com/admob/answer/9391084)

**Industry (non-authoritative)**
- [Stripe supported countries](https://stripe.com/global) — Nepal absent

---

## 16. Change log for this document

- **2026-09-30** — Initial version. Verified Play Payments policy, merchant
  registration table, AdMob availability and Nepal payout methods, gambling/contest
  policy, withholding tax schedule, India alternative billing, US external content
  links. Open questions in §14 unresolved.
