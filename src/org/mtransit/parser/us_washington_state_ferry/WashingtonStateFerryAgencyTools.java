package org.mtransit.parser.us_washington_state_ferry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.mtransit.parser.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.Pair;
import org.mtransit.parser.SplitUtils;
import org.mtransit.parser.SplitUtils.RouteTripSpec;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.gtfs.data.GTripStop;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MTrip;
import org.mtransit.parser.mt.data.MTripStop;

// https://business.wsdot.wa.gov/Transit/csv_files/wsf/google_transit.zip
public class WashingtonStateFerryAgencyTools extends DefaultAgencyTools {

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			args = new String[3];
			args[0] = "input/gtfs.zip";
			args[1] = "../../mtransitapps/us-washington-state-ferry-android/res/raw/";
			args[2] = ""; // files-prefix
		}
		new WashingtonStateFerryAgencyTools().start(args);
	}

	private HashSet<String> serviceIds;

	@Override
	public void start(String[] args) {
		System.out.printf("\nGenerating Washington State ferry data...");
		long start = System.currentTimeMillis();
		// this.serviceIds = extractUsefulServiceIds(args, this, true); // 1 service ID by day
		super.start(args);
		System.out.printf("\nGenerating Washington State ferry data... DONE in %s.\n", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludeCalendar(GCalendar gCalendar) {
		if (this.serviceIds != null) {
			return excludeUselessCalendar(gCalendar, this.serviceIds);
		}
		return super.excludeCalendar(gCalendar);
	}

	@Override
	public boolean excludeCalendarDate(GCalendarDate gCalendarDates) {
		if (this.serviceIds != null) {
			return excludeUselessCalendarDate(gCalendarDates, this.serviceIds);
		}
		return super.excludeCalendarDate(gCalendarDates);
	}

	@Override
	public boolean excludeTrip(GTrip gTrip) {
		if (this.serviceIds != null) {
			return excludeUselessTrip(gTrip, this.serviceIds);
		}
		return super.excludeTrip(gTrip);
	}

	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_FERRY;
	}

	private static final String SPACE = " ";

	private static final String ANACORTES_RSN = "ANC";
	private static final String BAINBRIDGE_ISLAND_RSN = "BNB";
	private static final String BREMERTON_RSN = "BRM";
	private static final String CLINTON_RSN = "CLN";
	private static final String COUPEVILLE_RSN = "CPV";
	private static final String EDMONDS_RSN = "EDM";
	private static final String FAUNTLEROY_RSN = "FNT";
	private static final String FRIDAY_HARBOR_RSN = "FRD";
	private static final String KINGSTON_RSN = "KNG";
	private static final String LOPEZ_ISLAND_RSN = "LPZ";
	private static final String MULKITEO_RSN = "MLK";
	private static final String ORCAS_ISLAND_RSN = "ORC";
	private static final String POINT_DEFIANCE_RSN = "PDF";
	private static final String PORT_TOWNSEND_RSN = "PTW";
	private static final String SEATTLE_RSN = "STL";
	private static final String SHAW_ISLAND_RSN = "SWI";
	private static final String SOUTHWORTH_RSN = "STH";
	private static final String TAHLEQUAH_RSN = "THL";
	private static final String VASHON_ISLAND_RSN = "VSH";

	@Override
	public String getRouteShortName(GRoute gRoute) {
		if (StringUtils.isEmpty(gRoute.getRouteShortName())) {
			int rsn = Integer.parseInt(gRoute.getRouteId());
			switch (rsn) {
			// @formatter:off
			case 37: return BAINBRIDGE_ISLAND_RSN + SPACE + SEATTLE_RSN; // Bainbridge Island - Seattle
			case 47: return BREMERTON_RSN + SPACE + SEATTLE_RSN; // Bremerton - Seattle
			case 73: return SEATTLE_RSN + SPACE + BAINBRIDGE_ISLAND_RSN; // Seattle - Bainbridge Island
			case 74: return SEATTLE_RSN + SPACE + BREMERTON_RSN; // Seattle - Bremerton
			case 101: return FRIDAY_HARBOR_RSN + SPACE + ANACORTES_RSN; // Friday Harbor - Anacortes
			case 110: return ANACORTES_RSN + SPACE + FRIDAY_HARBOR_RSN; // Anacortes - Friday Harbor
			case 113: return ANACORTES_RSN + SPACE + LOPEZ_ISLAND_RSN; // Anacortes - Lopez Island
			case 115: return ANACORTES_RSN + SPACE + ORCAS_ISLAND_RSN; // Anacortes - Orcas Island
			case 118: return ANACORTES_RSN + SPACE + SHAW_ISLAND_RSN; // Anacortes - Shaw Island
			case 128: return KINGSTON_RSN + SPACE + EDMONDS_RSN; // Kingston - Edmonds
			case 131: return LOPEZ_ISLAND_RSN + SPACE + ANACORTES_RSN; // Lopez Island - Anacortes
			case 145: return MULKITEO_RSN + SPACE + CLINTON_RSN; // Mukilteo - Clinton
			case 151: return ORCAS_ISLAND_RSN + SPACE + ANACORTES_RSN; // Orcas Island - Anacortes
			case 181: return SHAW_ISLAND_RSN + SPACE + ANACORTES_RSN; // Shaw Island - Anacortes
			case 209: return SOUTHWORTH_RSN + SPACE + FAUNTLEROY_RSN; // Southworth - Fauntleroy
			case 229: return VASHON_ISLAND_RSN + SPACE + FAUNTLEROY_RSN; // Vashon Island - Fauntleroy
			case 514: return CLINTON_RSN + SPACE + MULKITEO_RSN; // Clinton - Mukilteo
			case 812: return EDMONDS_RSN + SPACE + KINGSTON_RSN; // Edmonds - Kingston
			case 920: return FAUNTLEROY_RSN + SPACE + SOUTHWORTH_RSN; // Fauntleroy - Southworth
			case 922: return FAUNTLEROY_RSN + SPACE + VASHON_ISLAND_RSN; // Fauntleroy - Vashon Island
			case 1013: return FRIDAY_HARBOR_RSN + SPACE + LOPEZ_ISLAND_RSN; // Friday Harbor - Lopez Island
			case 1015: return FRIDAY_HARBOR_RSN + SPACE + ORCAS_ISLAND_RSN; // Friday Harbor - Orcas Island
			case 1018: return FRIDAY_HARBOR_RSN + SPACE + SHAW_ISLAND_RSN; // Friday Harbor - Shaw Island
			case 1117: return COUPEVILLE_RSN + SPACE + PORT_TOWNSEND_RSN; // Coupeville - Port Townsend
			case 1310: return LOPEZ_ISLAND_RSN + SPACE + FRIDAY_HARBOR_RSN; // Lopez Island - Friday Harbor
			case 1315: return LOPEZ_ISLAND_RSN + SPACE + ORCAS_ISLAND_RSN; // Lopez Island - Orcas Island
			case 1318: return LOPEZ_ISLAND_RSN + SPACE + SHAW_ISLAND_RSN; // Lopez Island - Shaw Island
			case 1510: return ORCAS_ISLAND_RSN + SPACE + FRIDAY_HARBOR_RSN; // Orcas Island - Friday Harbor
			case 1513: return ORCAS_ISLAND_RSN + SPACE + LOPEZ_ISLAND_RSN; // Orcas Island - Lopez Island
			case 1518: return ORCAS_ISLAND_RSN + SPACE + SHAW_ISLAND_RSN; // Orcas Island - Shaw Island
			case 1621: return POINT_DEFIANCE_RSN + SPACE + TAHLEQUAH_RSN; // Point Defiance - Tahlequah
			case 1711: return PORT_TOWNSEND_RSN + SPACE + COUPEVILLE_RSN; // Port Townsend - Coupeville
			case 1810: return SHAW_ISLAND_RSN + SPACE + FRIDAY_HARBOR_RSN; // Shaw Island - Friday Harbor
			case 1813: return SHAW_ISLAND_RSN + SPACE + LOPEZ_ISLAND_RSN; // Shaw Island - Lopez Island
			case 1815: return SHAW_ISLAND_RSN + SPACE + ORCAS_ISLAND_RSN; // Shaw Island - Orcas Island
			case 2022: return SOUTHWORTH_RSN + SPACE + VASHON_ISLAND_RSN; // Southworth - Vashon Island
			case 2116: return TAHLEQUAH_RSN + SPACE + POINT_DEFIANCE_RSN; // Tahlequah - Point Defiance
			case 2220: return VASHON_ISLAND_RSN + SPACE + SOUTHWORTH_RSN; // Vashon Island - Southworth
			// @formatter:on
			}
			System.out.printf("\nUnexpected route short name %s!\n", gRoute);
			System.exit(-1);
			return null;
		}
		return super.getRouteShortName(gRoute);
	}

	private static final String AGENCY_COLOR_GREEN = "007B63"; // GREEN (from logo SVG)

	private static final String AGENCY_COLOR = AGENCY_COLOR_GREEN;

	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	private static HashMap<Long, RouteTripSpec> ALL_ROUTE_TRIPS2;
	static {
		HashMap<Long, RouteTripSpec> map2 = new HashMap<Long, RouteTripSpec>();
		ALL_ROUTE_TRIPS2 = map2;
	}

	@Override
	public int compareEarly(long routeId, List<MTripStop> list1, List<MTripStop> list2, MTripStop ts1, MTripStop ts2, GStop ts1GStop, GStop ts2GStop) {
		if (ALL_ROUTE_TRIPS2.containsKey(routeId)) {
			return ALL_ROUTE_TRIPS2.get(routeId).compare(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop);
		}
		return super.compareEarly(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop);
	}

	@Override
	public ArrayList<MTrip> splitTrip(MRoute mRoute, GTrip gTrip, GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return ALL_ROUTE_TRIPS2.get(mRoute.getId()).getAllTrips();
		}
		return super.splitTrip(mRoute, gTrip, gtfs);
	}

	@Override
	public Pair<Long[], Integer[]> splitTripStop(MRoute mRoute, GTrip gTrip, GTripStop gTripStop, ArrayList<MTrip> splitTrips, GSpec routeGTFS) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return SplitUtils.splitTripStop(mRoute, gTrip, gTripStop, routeGTFS, ALL_ROUTE_TRIPS2.get(mRoute.getId()));
		}
		return super.splitTripStop(mRoute, gTrip, gTripStop, splitTrips, routeGTFS);
	}

	@Override
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return; // split
		}
		int directionId = gTrip.getDirectionId() == null ? 0 : gTrip.getDirectionId();
		mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), directionId);
	}

	@Override
	public boolean mergeHeadsign(MTrip mTrip, MTrip mTripToMerge) {
		System.out.printf("\nUnexpected trips to merge: %s & %s!\n", mTrip, mTripToMerge);
		System.exit(-1);
		return false;
	}

	private static final Pattern TO = Pattern.compile("((^|\\W){1}(to)(\\W|$){1})", Pattern.CASE_INSENSITIVE);

	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		Matcher matcherTO = TO.matcher(tripHeadsign);
		if (matcherTO.find()) {
			String gTripHeadsignAfterTO = tripHeadsign.substring(matcherTO.end());
			tripHeadsign = gTripHeadsignAfterTO;
		}
		tripHeadsign = CleanUtils.cleanSlashes(tripHeadsign);
		tripHeadsign = CleanUtils.CLEAN_AT.matcher(tripHeadsign).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		tripHeadsign = CleanUtils.CLEAN_AND.matcher(tripHeadsign).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanNumbers(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	@Override
	public String cleanStopName(String gStopName) {
		gStopName = CleanUtils.SAINT.matcher(gStopName).replaceAll(CleanUtils.SAINT_REPLACEMENT);
		gStopName = CleanUtils.CLEAN_AND.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		gStopName = CleanUtils.CLEAN_AT.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		gStopName = CleanUtils.cleanSlashes(gStopName);
		gStopName = CleanUtils.removePoints(gStopName);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}
}
