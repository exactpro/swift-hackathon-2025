import { z } from 'zod'

/**
 * Transfer status enum matching server-side TransferStatus
 */
export const TransferStatusSchema = z.enum(['PENDING', 'COMPLETED', 'FAILED'])
export type TransferStatus = z.infer<typeof TransferStatusSchema>

/**
 * Account entity schema
 */
export const AccountSchema = z.object({
  iban: z.string(),
  clientId: z.number().int(),
  currencyCode: z.string(),
  balance: z.number()
})
export type Account = z.infer<typeof AccountSchema>

/**
 * Transfer details schema (used for creating transfers)
 */
export const TransferDetailsSchema = z.object({
  debtorIban: z.string(),
  creditorFullName: z.string(),
  creditorBic: z.string(),
  creditorIban: z.string(),
  currencyCode: z.string(),
  amount: z.number().positive(),
  comment: z.string().optional()
})
export type TransferDetails = z.infer<typeof TransferDetailsSchema>

/**
 * Transfer entity schema (full transfer object)
 */
export const TransferSchema = z.object({
  transferId: z.number().int().optional(),
  clientId: z.number().int(),
  status: TransferStatusSchema,
  messageId: z.string().optional(),
  transferTimestamp: z.string().datetime().optional(), // ISO 8601 string
  endToEndId: z.string().optional(),
  currency: z.string(),
  amount: z.number(),
  settlementDate: z.string().optional(), // ISO date string (YYYY-MM-DD)
  debtorFullName: z.string().optional(),
  debtorIban: z.string(),
  debtorBic: z.string().optional(),
  creditorFullName: z.string(),
  creditorIban: z.string(),
  creditorBic: z.string(),
  remittanceInfo: z.string().optional()
})
export type Transfer = z.infer<typeof TransferSchema>
