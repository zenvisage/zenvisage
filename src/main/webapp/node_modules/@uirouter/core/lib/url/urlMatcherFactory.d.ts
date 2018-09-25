import { UrlMatcher } from './urlMatcher';
import { Param, ParamType, ParamTypeDefinition } from '../params';
import { UrlMatcherCompileConfig } from './interface';
import { StateDeclaration } from '../state';
import { UIRouter } from '../router';
/** @internalapi */
export declare class ParamFactory {
    private router;
    fromConfig(id: string, type: ParamType, state: StateDeclaration): Param;
    fromPath(id: string, type: ParamType, state: StateDeclaration): Param;
    fromSearch(id: string, type: ParamType, state: StateDeclaration): Param;
    constructor(router: UIRouter);
}
/**
 * Factory for [[UrlMatcher]] instances.
 *
 * The factory is available to ng1 services as
 * `$urlMatcherFactory` or ng1 providers as `$urlMatcherFactoryProvider`.
 *
 * @internalapi
 */
export declare class UrlMatcherFactory {
    private router;
    /** @internalapi Creates a new [[Param]] for a given location (DefType) */
    paramFactory: ParamFactory;
    constructor(/** @hidden */ router: UIRouter);
    /**
     * Creates a [[UrlMatcher]] for the specified pattern.
     *
     * @param pattern  The URL pattern.
     * @param config  The config object hash.
     * @returns The UrlMatcher.
     */
    compile(pattern: string, config?: UrlMatcherCompileConfig): UrlMatcher;
    /**
     * Returns true if the specified object is a [[UrlMatcher]], or false otherwise.
     *
     * @param object  The object to perform the type check against.
     * @returns `true` if the object matches the `UrlMatcher` interface, by
     *          implementing all the same methods.
     */
    isMatcher(object: any): boolean;
    /** @hidden */
    $get(): this;
    /** @deprecated use [[UrlConfig.caseInsensitive]] */
    caseInsensitive: (value?: boolean) => boolean;
    /** @deprecated use [[UrlConfig.defaultSquashPolicy]] */
    defaultSquashPolicy: (value?: string | boolean) => string | boolean;
    /** @deprecated use [[UrlConfig.strictMode]] */
    strictMode: (value?: boolean) => boolean;
    /** @deprecated use [[UrlConfig.type]] */
    type: (name: string, definition?: ParamTypeDefinition, definitionFn?: () => ParamTypeDefinition) => any;
}
