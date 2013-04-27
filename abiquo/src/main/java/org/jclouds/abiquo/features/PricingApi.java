/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.abiquo.features;

import java.io.Closeable;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.abiquo.binders.BindToPath;
import org.jclouds.abiquo.binders.BindToXMLPayloadAndPath;
import org.jclouds.abiquo.http.filters.AbiquoAuthentication;
import org.jclouds.abiquo.http.filters.AppendApiVersionToMediaType;
import org.jclouds.abiquo.rest.annotations.EndpointLink;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;

import com.abiquo.server.core.pricing.CostCodeCurrenciesDto;
import com.abiquo.server.core.pricing.CostCodeDto;
import com.abiquo.server.core.pricing.CostCodesDto;
import com.abiquo.server.core.pricing.CurrenciesDto;
import com.abiquo.server.core.pricing.CurrencyDto;
import com.abiquo.server.core.pricing.PricingCostCodeDto;
import com.abiquo.server.core.pricing.PricingCostCodesDto;
import com.abiquo.server.core.pricing.PricingTemplateDto;
import com.abiquo.server.core.pricing.PricingTemplatesDto;
import com.abiquo.server.core.pricing.PricingTierDto;
import com.abiquo.server.core.pricing.PricingTiersDto;

/**
 * Provides synchronous access to Abiquo Pricing API.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/APIReference">
 *      http://community.abiquo.com/display/ABI20/APIReference</a>
 * @author Ignasi Barrera
 * @author Susana Acedo
 */
@RequestFilters({ AbiquoAuthentication.class, AppendApiVersionToMediaType.class })
@Path("/config")
public interface PricingApi extends Closeable {

   /*********************** Currency ********************** */

   /**
    * List all currencies
    * 
    * @return The list of currencies
    */
   @Named("currency:list")
   @GET
   @Path("/currencies")
   @Consumes(CurrenciesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   CurrenciesDto listCurrencies();

   /**
    * Get the given currency
    * 
    * @param currencyId
    *           The id of the currency
    * @return The currency
    */
   @Named("currency:get")
   @GET
   @Path("/currencies/{currency}")
   @Consumes(CurrencyDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   CurrencyDto getCurrency(@PathParam("currency") Integer currencyId);

   /**
    * Create a new currency
    * 
    * @param currency
    *           The currency to be created.
    * @return The created currency.
    */
   @Named("currency:create")
   @POST
   @Path("/currencies")
   @Produces(CurrencyDto.BASE_MEDIA_TYPE)
   @Consumes(CurrencyDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   CurrencyDto createCurrency(@BinderParam(BindToXMLPayload.class) CurrencyDto currency);

   /**
    * Updates an existing currency
    * 
    * @param currency
    *           The new attributes for the currency
    * @return The updated currency
    */
   @Named("currency:update")
   @PUT
   @Produces(CurrencyDto.BASE_MEDIA_TYPE)
   @Consumes(CurrencyDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   CurrencyDto updateCurrency(@EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) CurrencyDto currency);

   /**
    * Deletes an existing currency
    * 
    * @param currency
    *           The currency to delete
    */
   @Named("currency:delete")
   @DELETE
   void deleteCurrency(@EndpointLink("edit") @BinderParam(BindToPath.class) CurrencyDto currency);

   /*********************** CostCode ********************** */

   /**
    * List all costcodes
    * 
    * @return The list of costcodes
    */
   @Named("costcode:list")
   @GET
   @Path("/costcodes")
   @Consumes(CostCodesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   CostCodesDto listCostCodes();

   /**
    * Get the given costcode
    * 
    * @param costcodeId
    *           The id of the costcode
    * @return The costcode
    */
   @Named("costcode:get")
   @GET
   @Path("/costcodes/{costcode}")
   @Consumes(CostCodeDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   CostCodeDto getCostCode(@PathParam("costcode") Integer costcodeId);

   /**
    * Create a new costcode
    * 
    * @param costcode
    *           The costcode to be created.
    * @return The created costcode.
    */
   @Named("costcode:create")
   @POST
   @Path("/costcodes")
   @Produces(CostCodeDto.BASE_MEDIA_TYPE)
   @Consumes(CostCodeDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   CostCodeDto createCostCode(@BinderParam(BindToXMLPayload.class) CostCodeDto costcode);

   /**
    * Updates an existing costcode
    * 
    * @param costcode
    *           The new attributes for the costcode
    * @return The updated costcode
    */
   @Named("costcode:update")
   @PUT
   @Produces(CostCodeDto.BASE_MEDIA_TYPE)
   @Consumes(CostCodeDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   CostCodeDto updateCostCode(@EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) CostCodeDto costcode);

   /**
    * Deletes an existing costcode
    * 
    * @param currency
    *           The costcode to delete
    */
   @Named("costcode:delete")
   @DELETE
   void deleteCostCode(@EndpointLink("edit") @BinderParam(BindToPath.class) CostCodeDto costcode);

   /*********************** PricingTemplate ********************** */

   /**
    * List all pricingtemplates
    * 
    * @return The list of pricingtemplates
    */
   @Named("pricingtemplate:list")
   @GET
   @Path("/pricingtemplates")
   @Consumes(PricingTemplatesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   PricingTemplatesDto listPricingTemplates();

   /**
    * Get the given pricingtemplate
    * 
    * @param pricingTemplateId
    *           The id of the pricingtemplate
    * @return The pricingtemplate
    */
   @Named("pricingtemplate:get")
   @GET
   @Path("/pricingtemplates/{pricingtemplate}")
   @Consumes(PricingTemplateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   PricingTemplateDto getPricingTemplate(@PathParam("pricingtemplate") Integer pricingTemplateId);

   /**
    * Create a new pricing template
    * 
    * @param pricingtemplate
    *           The pricingtemplate to be created
    * @return The created pricingtemplate
    */
   @Named("pricingtemplate:create")
   @POST
   @Path("/pricingtemplates")
   @Produces(PricingTemplateDto.BASE_MEDIA_TYPE)
   @Consumes(PricingTemplateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   PricingTemplateDto createPricingTemplate(@BinderParam(BindToXMLPayload.class) PricingTemplateDto pricingtemplate);

   /**
    * Updates an existing pricing template
    * 
    * @param pricingtemplate
    *           The new attributes for the pricingtemplate
    * @return The updated pricingtemplate
    */
   @Named("pricingtemplate:update")
   @PUT
   @Produces(PricingTemplateDto.BASE_MEDIA_TYPE)
   @Consumes(PricingTemplateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   PricingTemplateDto updatePricingTemplate(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) PricingTemplateDto pricingtemplate);

   /**
    * Deletes an existing pricingtemplate
    * 
    * @param pricingtemplate
    *           The pricingtemplate to delete
    */
   @Named("pricingtemplate:delete")
   @DELETE
   void deletePricingTemplate(@EndpointLink("edit") @BinderParam(BindToPath.class) PricingTemplateDto pricingtemplate);

   /*********************** CostCodeCurrency ********************** */

   /**
    * Get the given costcodecurrency
    * 
    * @param costcodecurrencyId
    *           The id of the costcodecurrency
    * @return The costcodecurrency
    */
   @Named("costcodecurrency:get")
   @GET
   @Path("/costcodes/{costcode}/currencies")
   @Consumes(CostCodeCurrenciesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   CostCodeCurrenciesDto getCostCodeCurrencies(@PathParam("costcode") Integer costcodeId,
         @QueryParam("idCurrency") Integer currencyId);

   /**
    * Updates cost code currencies
    * 
    * @param costcodeCurrency
    *           The new attributes for the costcodecurrencies
    * @return The updated costcodecurrencies
    */
   @Named("costcodecurrency:update")
   @PUT
   @Path("/costcodes/{costcode}/currencies")
   @Produces(CostCodeCurrenciesDto.BASE_MEDIA_TYPE)
   @Consumes(CostCodeCurrenciesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   CostCodeCurrenciesDto updateCostCodeCurrencies(@PathParam("costcode") Integer costcodeId,
         @BinderParam(BindToXMLPayload.class) CostCodeCurrenciesDto costcodecurrencies);

   /*********************** PricingTemplateCostCode ********************** */

   /**
    * Get the pricing cost codes for a pricing template
    * 
    * @param pricingTemplateId
    * @return pricingcostcodes
    */
   @Named("pricingcostcode:get")
   @GET
   @Path("/pricingtemplates/{pricingtemplate}/costcodes")
   @Consumes(PricingCostCodesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   PricingCostCodesDto getPricingCostCodes(@PathParam("pricingtemplate") Integer pricingTemplateId);

   /**
    * Get the given pricing cost code
    * 
    * @param pricingCostCodeId
    *           the id of the pricing cost code
    * @return The pricingcostcode
    */
   @Named("pricingcostcode:get")
   @GET
   @Path("/pricingtemplates/{pricingtemplate}/costcodes/{costcode}")
   @Consumes(PricingCostCodeDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   PricingCostCodeDto getPricingCostCode(@PathParam("pricingtemplate") Integer pricingTemplateId,
         @PathParam("costcode") Integer pricingCostcodeId);

   /**
    * Updates an existing pricingcostcode
    * 
    * @param costcodeCurrency
    *           The new attributes for the pricingcostcode
    * @return The updated pricingcostcode
    */
   @Named("pricingcostcode:update")
   @PUT
   @Path("/pricingtemplates/{pricingtemplate}/costcodes/{costcode}")
   @Produces(PricingCostCodeDto.BASE_MEDIA_TYPE)
   @Consumes(PricingCostCodeDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   PricingCostCodeDto updatePricingCostCode(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) PricingCostCodeDto pricingcostcode,
         @PathParam("pricingtemplate") Integer pricingTemplateId, @PathParam("costcode") Integer pricingCostcodeId);

   /*********************** PricingTemplateTier ********************** */

   /**
    * Get the pricing tiers for a pricing template
    * 
    * @param pricingTemplateId
    * @return pricingtiers
    */
   @Named("pricingtier:get")
   @GET
   @Path("/pricingtemplates/{pricingtemplate}/tiers")
   @Consumes(PricingTiersDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   PricingTiersDto getPricingTiers(@PathParam("pricingtemplate") Integer pricingTemplateId);

   /**
    * Get the given pricing tier
    * 
    * @param pricingTierId
    *           The id of the pricing tier
    * @return The pricingtier
    */
   @Named("pricingtier:get")
   @GET
   @Path("/pricingtemplates/{pricingtemplate}/tiers/{tier}")
   @Consumes(PricingTierDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   PricingTierDto getPricingTier(@PathParam("pricingtemplate") Integer pricingTemplateId,
         @PathParam("tier") Integer pricingTierId);

   /**
    * Updates an existing pricing tier
    * 
    * @param costcodeCurrency
    *           The new attributes for the pricing tier
    * @return The updated pricing tier
    */
   @Named("pricingtier:update")
   @PUT
   @Path("/pricingtemplates/{pricingtemplate}/tiers/{tier}")
   @Produces(PricingTierDto.BASE_MEDIA_TYPE)
   @Consumes(PricingTierDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   PricingTierDto updatePricingTier(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) PricingTierDto pricingtier,
         @PathParam("pricingtemplate") Integer pricingTemplateId, @PathParam("tier") Integer pricingTierId);
}
